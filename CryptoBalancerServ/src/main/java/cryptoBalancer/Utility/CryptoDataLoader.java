package cryptoBalancer.Utility;

import cryptoBalancer.Models.Entities.CryptoHistory;
import cryptoBalancer.Services.CryptoHistoryService;
import cryptoBalancer.Services.CryptoService;
import me.joshmcfarlin.cryptocompareapi.Coins;
import me.joshmcfarlin.cryptocompareapi.CryptoCompareAPI;

import java.util.List;

public class CryptoDataLoader {
    private final CryptoService cryptoService = new CryptoService();
    private final CryptoHistoryService cryptoHistory = new CryptoHistoryService();
    private final CryptoCompareAPI api = new CryptoCompareAPI();

    public void loadAllCoins(){

    }
}
