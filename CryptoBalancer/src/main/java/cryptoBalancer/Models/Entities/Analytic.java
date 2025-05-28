package cryptoBalancer.Models.Entities;

import com.google.gson.annotations.Expose;

import java.math.BigDecimal;

public class Analytic {
    @Expose
    private int portfolioId;

    private Portfolio portfolio;
    @Expose
    private BigDecimal expectedReturn;
    @Expose
    private BigDecimal risk;

    public Analytic() {
    }

    public Analytic(int portfolioId, BigDecimal risk, Portfolio portfolio, BigDecimal expectedReturn) {
        this.portfolioId = portfolioId;
        this.risk = risk;
        this.portfolio = portfolio;
        this.expectedReturn = expectedReturn;
    }

    public int getPortfolioId() {
        return portfolioId;
    }

    public void setPortfolioId(int portfolioId) {
        this.portfolioId = portfolioId;
    }

    public BigDecimal getRisk() {
        return risk;
    }

    public void setRisk(BigDecimal risk) {
        this.risk = risk;
    }

    public BigDecimal getExpectedReturn() {
        return expectedReturn;
    }

    public void setExpectedReturn(BigDecimal expectedReturn) {
        this.expectedReturn = expectedReturn;
    }

    public Portfolio getPortfolio() {
        return portfolio;
    }

    public void setPortfolio(Portfolio portfolio) {
        this.portfolio = portfolio;
    }
}
