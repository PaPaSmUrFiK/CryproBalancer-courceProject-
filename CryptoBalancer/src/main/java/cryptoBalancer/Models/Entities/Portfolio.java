package cryptoBalancer.Models.Entities;

import com.google.gson.annotations.Expose;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

public class Portfolio {
    @Expose
    private int portfolioId;
    @Expose
    private String portfolioName;
    @Expose
    private LocalDateTime createdAt;
    @Expose
    private Analytic analytic;
    @Expose
    private User user;
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
