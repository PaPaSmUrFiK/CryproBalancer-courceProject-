package cryptoBalancer.Models.Entities;

import com.google.gson.annotations.Expose;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "cryptohistory", schema = "crypto")
public class CryptoHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "history_id")
    @Expose
    private int historyId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "crypto_id", nullable = false)
    @Expose
    private Crypto crypto;

    @Column(name = "date_changed", nullable = false)
    @Expose
    private LocalDate dateChanged;

    @Column(name = "price", nullable = false, precision = 20, scale = 8)
    @Expose
    private BigDecimal price;

    public CryptoHistory() {
    }

    public CryptoHistory(int historyId, BigDecimal price, LocalDate dateChanged, Crypto crypto) {
        this.historyId = historyId;
        this.price = price;
        this.dateChanged = dateChanged;
        this.crypto = crypto;
    }

    public int getHistoryId() {
        return historyId;
    }

    public void setHistoryId(int historyId) {
        this.historyId = historyId;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public LocalDate getDateChanged() {
        return dateChanged;
    }

    public void setDateChanged(LocalDate dateChanged) {
        this.dateChanged = dateChanged;
    }

    public Crypto getCrypto() {
        return crypto;
    }

    public void setCrypto(Crypto crypto) {
        this.crypto = crypto;
    }
}
