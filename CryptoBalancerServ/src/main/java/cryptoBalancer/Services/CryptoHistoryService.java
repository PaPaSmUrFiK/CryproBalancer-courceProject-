package cryptoBalancer.Services;

import cryptoBalancer.DataAccessObjects.CryptoHistoryDAO;
import cryptoBalancer.Interfaces.DAO;
import cryptoBalancer.Interfaces.Service;
import cryptoBalancer.Models.Entities.CryptoHistory;

import java.util.List;

public class CryptoHistoryService implements Service<CryptoHistory> {
    DAO<CryptoHistory> daoService = new CryptoHistoryDAO();

    @Override
    public CryptoHistory findEntity(int id) {
        return daoService.findById(id);
    }

    @Override
    public void saveEntity(CryptoHistory entity) {
        daoService.save(entity);
    }

    @Override
    public void deleteEntity(CryptoHistory entity) {
        daoService.delete(entity);
    }

    @Override
    public void updateEntity(CryptoHistory entity) {
        daoService.update(entity);
    }

    public List<CryptoHistory> findAllEntities() {
        return daoService.findAll();
    }

}
