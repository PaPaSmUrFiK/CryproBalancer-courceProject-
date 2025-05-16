package cryptoBalancer.Services;

import cryptoBalancer.DataAccessObjects.CryptoDAO;
import cryptoBalancer.DataAccessObjects.UserDAO;
import cryptoBalancer.Interfaces.DAO;
import cryptoBalancer.Interfaces.Service;
import cryptoBalancer.Models.Entities.Crypto;
import cryptoBalancer.Models.Entities.User;

import java.util.List;

public class CryptoService implements Service<Crypto> {
    DAO<Crypto> daoService = new CryptoDAO();

    @Override
    public Crypto findEntity(int id) {
        return daoService.findById(id);
    }

    @Override
    public void saveEntity(Crypto entity) {
        daoService.save(entity);
    }

    @Override
    public void deleteEntity(Crypto entity) {
        daoService.delete(entity);
    }

    @Override
    public void updateEntity(Crypto entity) {
        daoService.update(entity);
    }

    @Override
    public List<Crypto> findAllEntities() {
        return daoService.findAll();
    }

    public Crypto findCryptoByName(String cryptoName){
        return ((CryptoDAO)daoService).findCryptoByName(cryptoName);
    }
}
