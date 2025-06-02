package cryptoBalancer.Utility;

import cryptoBalancer.Models.Entities.Crypto;
import cryptoBalancer.Models.Entities.CryptoHistory;
import cryptoBalancer.Models.Entities.Investment;
import cryptoBalancer.Services.CryptoHistoryService;
import org.apache.commons.math3.stat.correlation.Covariance;
import org.ojalgo.matrix.Primitive64Matrix;
import org.ojalgo.optimisation.Expression;
import org.ojalgo.optimisation.ExpressionsBasedModel;
import org.ojalgo.optimisation.Optimisation;
import org.ojalgo.optimisation.Variable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class MarkowitzOptimizer {
    private final CryptoHistoryService cryptoHistoryService;

    public MarkowitzOptimizer(CryptoHistoryService cryptoHistoryService) {
        this.cryptoHistoryService = cryptoHistoryService;
    }

    private static class PortfolioData {
        final double[][] covarianceMatrix;
        final double[] expectedReturns;
        final int nAssets;
        final List<List<CryptoHistory>> histories;

        PortfolioData(double[][] covarianceMatrix, double[] expectedReturns, int nAssets, List<List<CryptoHistory>> histories) {
            this.covarianceMatrix = covarianceMatrix;
            this.expectedReturns = expectedReturns;
            this.nAssets = nAssets;
            this.histories = histories;
        }
    }

    private PortfolioData getPortfolioData(Set<Investment> investments) {
        int nAssets = investments.size();
        if (nAssets == 0) {
            throw new IllegalArgumentException("Нет предоставленных инвестиций");
        }

        List<List<CryptoHistory>> histories = new ArrayList<>();
        for (Investment investment : investments) {
            Crypto crypto = investment.getCrypto();
            List<CryptoHistory> history = cryptoHistoryService.getLastNRecords(crypto.getCryptoId(), 365);
            
            // Проверяем, что у нас есть достаточно данных
            if (history.size() < 365) {
                throw new IllegalStateException(
                    String.format("Недостаточно исторических данных для %s. Требуется 365 дней, доступно %d", 
                        crypto.getName(), history.size())
                );
            }
            
            // Проверяем временной период
            LocalDate newestDate = history.get(0).getDateChanged();
            LocalDate oldestDate = history.get(history.size() - 1).getDateChanged();

            long daysBetween = ChronoUnit.DAYS.between(oldestDate, newestDate);
            
            if (daysBetween < 364) { // Учитываем возможные пропуски в данных
                throw new IllegalStateException(
                    String.format("История для %s не охватывает полный год. Доступный период: %d дней", 
                        crypto.getName(), daysBetween)
                );
            }
            
            histories.add(history);
        }

        int nDays = 365;
        double[][] returns = new double[nAssets][nDays - 1];
        for (int i = 0; i < nAssets; i++) {
            List<CryptoHistory> history = histories.get(i);
            for (int j = 0; j < nDays - 1; j++) {
                double priceToday = history.get(j).getPrice().doubleValue();
                double priceTomorrow = history.get(j + 1).getPrice().doubleValue();
                returns[i][j] = (priceTomorrow - priceToday) / priceToday;
            }
        }

        double[] expectedReturns = new double[nAssets];
        for (int i = 0; i < nAssets; i++) {
            double sum = 0;
            for (int j = 0; j < nDays - 1; j++) {
                sum += returns[i][j];
            }
            expectedReturns[i] = sum / (nDays - 1);
        }

        double[][] returnsTransposed = new double[nDays - 1][nAssets];
        for (int i = 0; i < nDays - 1; i++) {
            for (int j = 0; j < nAssets; j++) {
                returnsTransposed[i][j] = returns[j][i];
            }
        }

        Covariance covariance = new Covariance(returnsTransposed);
        double[][] covMatrix = covariance.getCovarianceMatrix().getData();

        return new PortfolioData(covMatrix, expectedReturns, nAssets, histories);
    }

    public void optimizePortfolio(Set<Investment> investments) {
        PortfolioData data = getPortfolioData(investments);
        int nAssets = data.nAssets;
        double[][] covMatrix = data.covarianceMatrix;

        ExpressionsBasedModel model = new ExpressionsBasedModel();
        List<Variable> weights = new ArrayList<>();
        for (int i = 0; i < nAssets; i++) {
            weights.add(model.addVariable("w" + i).lower(0).upper(1));
        }

        // Сумма весов = 1
        Expression sumExpr = model.addExpression("SumWeights");
        for (Variable weight : weights) {
            sumExpr.set(weight, 1.0);
        }
        sumExpr.level(1.0);

        // Минимизация дисперсии
        Expression variance = model.addExpression("Variance");
        variance.setQuadraticFactors(
                weights,
                Primitive64Matrix.FACTORY.rows(covMatrix)
        );
        variance.weight(1.0);

        Optimisation.Result result = model.minimise();
        if (result == null || !result.getState().isFeasible()) {
            throw new IllegalStateException("Не удалось найти оптимальное решение");
        }

        // Применение весов к инвестициям
        List<Investment> investmentList = new ArrayList<>(investments);
        for (int i = 0; i < nAssets; i++) {
            double weight = weights.get(i).getValue().doubleValue();
            Investment investment = investmentList.get(i);
            List<CryptoHistory> history = data.histories.get(i);
            BigDecimal latestPrice = history.get(history.size() - 1).getPrice();
            // Устанавливаем amount как процент (вес)
            BigDecimal percentage = BigDecimal.valueOf(weight);
            investment.setAmount(percentage);
            investment.setPurchasePrice(latestPrice);
        }
    }

    public void maximizeReturnForRisk(Set<Investment> investments, double maxVariance) {
        PortfolioData data = getPortfolioData(investments);
        int nAssets = data.nAssets;
        double[][] covMatrix = data.covarianceMatrix;
        double[] expectedReturns = data.expectedReturns;

        ExpressionsBasedModel model = new ExpressionsBasedModel();
        List<Variable> weights = new ArrayList<>();
        for (int i = 0; i < nAssets; i++) {
            weights.add(model.addVariable("w" + i).lower(0).upper(1));
        }

        // Сумма весов = 1
        Expression sumExpr = model.addExpression("SumWeights");
        for (Variable weight : weights) {
            sumExpr.set(weight, 1.0);
        }
        sumExpr.level(1.0);

        // Ограничение на риск
        Expression riskConstraint = model.addExpression("RiskConstraint");
        riskConstraint.setQuadraticFactors(
                weights,
                Primitive64Matrix.FACTORY.rows(covMatrix)
        );
        riskConstraint.upper(maxVariance);

        // Максимизация доходности
        Expression returnObj = model.addExpression("ReturnObjective");
        returnObj.setLinearFactors(
                weights,
                Primitive64Matrix.FACTORY.column(expectedReturns)
        );
        returnObj.weight(-1.0);

        Optimisation.Result result = model.minimise();
        if (result == null || !result.getState().isFeasible()) {
            throw new IllegalStateException("Не удалось найти оптимальное решение");
        }

        // Применение весов к инвестициям
        List<Investment> investmentList = new ArrayList<>(investments);
        for (int i = 0; i < nAssets; i++) {
            double weight = weights.get(i).getValue().doubleValue();
            Investment investment = investmentList.get(i);
            List<CryptoHistory> history = data.histories.get(i);
            BigDecimal latestPrice = history.get(history.size() - 1).getPrice();
            BigDecimal percentage = BigDecimal.valueOf(weight);
            investment.setAmount(percentage);
            investment.setPurchasePrice(latestPrice);
        }
    }

    public void minimizeRiskForReturn(Set<Investment> investments, double minReturn) {
        PortfolioData data = getPortfolioData(investments);
        int nAssets = data.nAssets;
        double[][] covMatrix = data.covarianceMatrix;
        double[] expectedReturns = data.expectedReturns;

        ExpressionsBasedModel model = new ExpressionsBasedModel();
        List<Variable> weights = new ArrayList<>();
        for (int i = 0; i < nAssets; i++) {
            weights.add(model.addVariable("w" + i).lower(0).upper(1));
        }

        // Сумма весов = 1
        Expression sumExpr = model.addExpression("SumWeights");
        for (Variable weight : weights) {
            sumExpr.set(weight, 1.0);
        }
        sumExpr.level(1.0);

        // Ограничение на доходность
        Expression returnConstraint = model.addExpression("ReturnConstraint");
        returnConstraint.setLinearFactors(
                weights,
                Primitive64Matrix.FACTORY.column(expectedReturns)
        );
        returnConstraint.lower(minReturn);

        // Минимизация дисперсии
        Expression variance = model.addExpression("Variance");
        variance.setQuadraticFactors(
                weights,
                Primitive64Matrix.FACTORY.rows(covMatrix)
        );
        variance.weight(1.0);

        Optimisation.Result result = model.minimise();
        if (result == null || !result.getState().isFeasible()) {
            throw new IllegalStateException("Не удалось найти оптимальное решение");
        }

        // Применение весов к инвестициям
        List<Investment> investmentList = new ArrayList<>(investments);
        for (int i = 0; i < nAssets; i++) {
            double weight = weights.get(i).getValue().doubleValue();
            Investment investment = investmentList.get(i);
            List<CryptoHistory> history = data.histories.get(i);
            BigDecimal latestPrice = history.get(history.size() - 1).getPrice();
            BigDecimal percentage = BigDecimal.valueOf(weight);
            investment.setAmount(percentage);
            investment.setPurchasePrice(latestPrice);
        }
    }
}