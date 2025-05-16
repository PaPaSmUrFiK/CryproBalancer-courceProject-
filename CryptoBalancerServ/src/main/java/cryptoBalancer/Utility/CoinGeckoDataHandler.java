package cryptoBalancer.Utility;

import com.litesoftwares.coingecko.CoinGeckoApiClient;
import com.litesoftwares.coingecko.domain.Coins.MarketChart;
import com.litesoftwares.coingecko.domain.Coins.CoinMarkets;
import com.litesoftwares.coingecko.impl.CoinGeckoApiClientImpl;
import cryptoBalancer.Models.Entities.Crypto;
import cryptoBalancer.Models.Entities.CryptoHistory;
import cryptoBalancer.Services.CryptoHistoryService;
import cryptoBalancer.Services.CryptoService;
import org.hibernate.SessionFactory;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class CoinGeckoDataHandler {
    private final CryptoService cryptoService;
    private final CryptoHistoryService cryptoHistoryService;
    private final CoinGeckoApiClient client;

    public CoinGeckoDataHandler() {
        this.cryptoService = new CryptoService();
        this.cryptoHistoryService = new CryptoHistoryService();
        this.client = new CoinGeckoApiClientImpl();
    }

    public void loadTopCoins(int quantity) {
        List<CoinMarkets> topCoins = client.getCoinMarkets(
                "usd",
                "",
                "market_cap_desc",
                quantity,
                1,
                false,
                ""
        );

        System.out.println("Loaded " + topCoins.size() + " cryptocurrencies from CoinGecko at " +
                LocalDate.now() + " " + java.time.LocalTime.now());

        for (CoinMarkets coin : topCoins) {
            Crypto crypto = new Crypto();
            crypto.setCoinGeckoId(coin.getId());
            crypto.setName(Optional.ofNullable(coin.getName()).orElse(coin.getId()));
            crypto.setSymbol(Optional.ofNullable(coin.getSymbol()).orElse(coin.getId()));

            try {
                cryptoService.updateEntity(crypto);
                loadHistoricalData(crypto, 365);
            } catch (Exception e) {
                System.err.println("Error processing " + coin.getId() + ": " + e.getMessage() + " - " + e.getClass().getName());
            }
        }
    }

    public void loadHistoricalData(Crypto crypto, int days) {
        String coinId = crypto.getCoinGeckoId();
        try {
            MarketChart marketChart = client.getCoinMarketChartById(coinId, "usd", days);
            List<List<String>> prices = marketChart.getPrices(); // Изменяем тип на List<List<String>>

            if (prices == null || prices.isEmpty()) {
                System.out.println("No data found for " + coinId);
                return;
            }

            Map<LocalDate, BigDecimal> dailyPrices = new LinkedHashMap<>();
            for (List<String> priceData : prices) {
                if (priceData.size() < 2) {
                    System.err.println("Invalid price data format for " + coinId + ": " + priceData);
                    continue;
                }

                // Преобразуем строки в числовые значения
                long timestamp;
                double price;
                try {
                    timestamp = Long.parseLong(priceData.get(0)); // Парсим timestamp
                    price = Double.parseDouble(priceData.get(1)); // Парсим цену
                } catch (NumberFormatException e) {
                    System.err.println("Failed to parse price data for " + coinId + ": " + priceData + " - " + e.getMessage());
                    continue;
                }

                LocalDate date = Instant.ofEpochMilli(timestamp)
                        .atZone(ZoneId.systemDefault())
                        .toLocalDate();

                dailyPrices.put(date, BigDecimal.valueOf(price));
            }

            for (Map.Entry<LocalDate, BigDecimal> entry : dailyPrices.entrySet()) {
                CryptoHistory cryptoHistory = new CryptoHistory();
                cryptoHistory.setCrypto(crypto);
                cryptoHistory.setDateChanged(entry.getKey());
                cryptoHistory.setPrice(entry.getValue());
                cryptoHistoryService.saveEntity(cryptoHistory);
            }

            System.out.println("Saved " + dailyPrices.size() + " historical records for " + coinId);
            Thread.sleep(2000); // Задержка для соответствия лимиту 30 запросов/мин
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("Thread interrupted for " + coinId + ": " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Error processing " + coinId + ": " + e.getMessage() + " - " + e.getClass().getName());
        }
    }

    public void shutdown() {
        if (client != null) {
            client.shutdown();
        }
    }
}