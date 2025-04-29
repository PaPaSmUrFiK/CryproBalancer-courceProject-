package cryptoBalancer.Models.Entities;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Crypto {
    private int cryptoId;

    private String name;

    private String symbol;

    private Set<Investment> investments = new HashSet<>();

    private List<CryptoHistory> history = new ArrayList<>();

    public Crypto() {
    }

    public Crypto(Set<Investment> investments, List<CryptoHistory> history, String symbol, String name, int cryptoId) {
        this.investments = investments;
        this.history = history;
        this.symbol = symbol;
        this.name = name;
        this.cryptoId = cryptoId;
    }

    public int getCryptoId() {
        return cryptoId;
    }

    public void setCryptoId(int cryptoId) {
        this.cryptoId = cryptoId;
    }

    public Set<Investment> getInvestments() {
        return investments;
    }

    public void setInvestments(Set<Investment> investments) {
        this.investments = investments;
    }

    public List<CryptoHistory> getHistory() {
        return history;
    }

    public void setHistory(List<CryptoHistory> history) {
        this.history = history;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
