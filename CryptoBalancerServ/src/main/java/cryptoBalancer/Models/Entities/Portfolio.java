package cryptoBalancer.Models.Entities;

import com.google.gson.annotations.Expose;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.*;

@Entity
@Table(name = "portfolio", schema = "crypto_balancer")
public class Portfolio{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "portfolio_id")
    @Expose
    private int portfolioId;

    @Column(name = "portfolio_name", nullable = false)
    @Expose
    private String portfolioName;

    @Column(name = "created_at", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    @Expose
    private LocalDateTime createdAt;

    @OneToOne(mappedBy = "portfolio", cascade = CascadeType.ALL, orphanRemoval = true)
    @Expose
    private Analytic analytic;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    @Expose
    private User user;

    @OneToMany(mappedBy = "portfolio", cascade = CascadeType.ALL, orphanRemoval = true)
    @Expose
    private Set<Investment> investments = new LinkedHashSet<>();

    public Portfolio() {
    }

    public Portfolio(int portfolioId, Set<Investment> investments, Analytic analytic, User user, LocalDateTime createdAt, String portfolioName) {
        this.portfolioId = portfolioId;
        this.investments = investments;
        this.analytic = analytic;
        this.user = user;
        this.createdAt = createdAt;
        this.portfolioName = portfolioName;
    }

    public int getPortfolioId() {
        return portfolioId;
    }

    public void setPortfolioId(int portfolioId) {
        this.portfolioId = portfolioId;
    }

    public Set<Investment> getInvestments() {
        return investments;
    }

    public void setInvestments(Set<Investment> investments) {
        this.investments = investments;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Analytic getAnalytic() {
        return analytic;
    }

    public void setAnalytic(Analytic analytic) {
        this.analytic = analytic;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getPortfolioName() {
        return portfolioName;
    }

    public void setPortfolioName(String portfolioName) {
        this.portfolioName = portfolioName;
    }
}