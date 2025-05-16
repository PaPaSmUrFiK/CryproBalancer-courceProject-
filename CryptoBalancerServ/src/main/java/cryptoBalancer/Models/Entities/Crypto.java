package cryptoBalancer.Models.Entities;

import com.google.gson.annotations.Expose;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "crypto", schema = "crypto_balancer")
public class Crypto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "crypto_id")
    @Expose
    private int cryptoId;

    @Column(name = "name", nullable = false)
    @Expose
    private String name;

    @Column(name = "symbol", nullable = false, unique = true)
    @Expose
    private String symbol;

    @Column(name = "coingecko_id", nullable = false, unique = true)
    @Expose
    private String coinGeckoId;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "crypto", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Investment> investments = new HashSet<>();

    @OneToMany(mappedBy = "crypto", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    @Expose
    private List<CryptoHistory> history = new ArrayList<>();

    public Crypto() {
    }

    public Crypto(Set<Investment> investments, List<CryptoHistory> history, String symbol, String name, int cryptoId, String coinGeckoId) {
        this.investments = investments;
        this.history = history;
        this.symbol = symbol;
        this.name = name;
        this.cryptoId = cryptoId;
        this.coinGeckoId = coinGeckoId;
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

    public String getCoinGeckoId() {
        return coinGeckoId;
    }

    public void setCoinGeckoId(String coinGeckoId) {
        this.coinGeckoId = coinGeckoId;
    }

    @Override
    public String toString() {
        return "Crypto{" +
                "coinGeckoId='" + coinGeckoId + '\'' +
                ", name='" + name + '\'' +
                ", symbol='" + symbol + '\'' +
                '}';
    }
}
