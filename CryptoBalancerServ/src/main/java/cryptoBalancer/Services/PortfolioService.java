package cryptoBalancer.Services;

import cryptoBalancer.DataAccessObjects.PortfolioDAO;
import cryptoBalancer.Interfaces.DAO;
import cryptoBalancer.Interfaces.Service;
import cryptoBalancer.Models.Entities.Portfolio;
import java.util.List;

public class PortfolioService implements Service<Portfolio> {
    DAO<Portfolio> daoService = new PortfolioDAO();

    @Override
    public Portfolio findEntity(int id) {
        return daoService.findById(id);
    }

    @Override
    public void saveEntity(Portfolio entity) {
        daoService.save(entity);
    }

    @Override
    public void deleteEntity(Portfolio entity) {
        daoService.delete(entity);
    }

    @Override
    public void updateEntity(Portfolio entity) {
        daoService.update(entity);
    }

    public List<Portfolio> findAllEntities() {
        return daoService.findAll();
    }
}
