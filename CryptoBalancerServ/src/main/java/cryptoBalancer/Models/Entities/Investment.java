package cryptoBalancer.Models.Entities;

import com.google.gson.annotations.Expose;
import jakarta.persistence.*;

import java.math.BigDecimal;


@Entity
@Table(name = "investment", schema = "crypto_balancer")
public class Investment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "investment_id")
    @Expose
    private int investmentId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "portfolio_id", nullable = false)
    private Portfolio portfolio;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "crypto_id", nullable = false)
    @Expose
    private Crypto crypto;

    @Column(name = "amount", nullable = false, precision = 20, scale = 8)
    @Expose
    private BigDecimal amount;

    @Column(name = "purchase_price", precision = 20, scale = 8)
    @Expose
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
