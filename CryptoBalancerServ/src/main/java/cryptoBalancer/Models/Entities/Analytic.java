package cryptoBalancer.Models.Entities;

import com.google.gson.annotations.Expose;
import jakarta.persistence.*;

import java.math.BigDecimal;

//@Entity
//@Table(name = "analytic", schema = "crypto")
//public class Analytic {
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    @Column(name = "analytic_id")
//    private int analyticId;
//    @Column(name = "expected_return", precision = 38, scale = 2)
//    private BigDecimal expectedReturn;
//    @Column(name = "risk", precision = 38, scale = 2)
//    private BigDecimal risk;
//    @OneToOne(fetch = FetchType.EAGER, mappedBy = "analytic")
//    private Portfolio portfolio;
//
//    public Analytic() {
//    }
//
//    public Analytic(Portfolio portfolio, BigDecimal risk, BigDecimal expectedReturn, int analyticId) {
//        this.portfolio = portfolio;
//        this.risk = risk;
//        this.expectedReturn = expectedReturn;
//        this.analyticId = analyticId;
//    }
//
//
//}

@Entity
@Table(name = "analytic", schema = "crypto_balancer")
public class Analytic {
    @Id
    @Column(name = "portfolio_id")
    @Expose
    private int portfolioId;

    @OneToOne
    @MapsId
    @JoinColumn(name = "portfolio_id")
    private Portfolio portfolio;

    @Column(name = "expected_return", nullable = false, precision = 10, scale = 6)
    @Expose
    private BigDecimal expectedReturn;

    @Column(name = "risk", nullable = false, precision = 10, scale = 6)
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
