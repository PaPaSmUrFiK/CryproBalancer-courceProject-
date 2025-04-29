package cryptoBalancer.Models.Entities;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class CryptoHistory {
    private int historyId;

    private Crypto crypto;

    private LocalDate dateChanged;

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
