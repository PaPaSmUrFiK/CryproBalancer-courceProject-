package cryptoBalancer.Models.Entities;

import java.math.BigDecimal;

public class Investment {
    private int investmentId;

    private Portfolio portfolio;

    private Crypto crypto;

    private BigDecimal amount;

    private BigDecimal purchasePrice;

    public Investment() {
    }

    public Investment(int investmentId, BigDecimal purchasePrice, BigDecimal amount, Portfolio portfolio, Crypto crypto) {
        this.investmentId = investmentId;
        this.purchasePrice = purchasePrice;
        this.amount = amount;
        this.portfolio = portfolio;
        this.crypto = crypto;
    }

    public int getInvestmentId() {
        return investmentId;
    }

    public void setInvestmentId(int investmentId) {
        this.investmentId = investmentId;
    }

    public BigDecimal getPurchasePrice() {
        return purchasePrice;
    }

    public void setPurchasePrice(BigDecimal purchasePrice) {
        this.purchasePrice = purchasePrice;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Portfolio getPortfolio() {
        return portfolio;
    }

    public void setPortfolio(Portfolio portfolio) {
        this.portfolio = portfolio;
    }

    public Crypto getCrypto() {
        return crypto;
    }

    public void setCrypto(Crypto crypto) {
        this.crypto = crypto;
    }
}

