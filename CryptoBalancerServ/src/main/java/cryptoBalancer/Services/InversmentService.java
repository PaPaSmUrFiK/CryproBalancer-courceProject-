package cryptoBalancer.Services;

import cryptoBalancer.DataAccessObjects.InvestmentDAO;
import cryptoBalancer.Interfaces.DAO;
import cryptoBalancer.Interfaces.Service;
import cryptoBalancer.Models.Entities.Investment;

import java.util.List;

public class InversmentService implements Service<Investment> {
    DAO<Investment> daoService = new InvestmentDAO();

    @Override
    public Investment findEntity(int id) {
        return daoService.findById(id);
    }

    @Override
    public void saveEntity(Investment entity) {
        daoService.save(entity);
    }

    @Override
    public void deleteEntity(Investment entity) {
        daoService.delete(entity);
    }

    @Override
    public void updateEntity(Investment entity) {
        daoService.update(entity);
    }

    public List<Investment> findAllEntities() {
        return daoService.findAll();
    }

    public void deleteInvestmentsByPortfolioId(int portfolioId){
        ((InvestmentDAO)daoService).deleteInvestmentsByPortfolioId(portfolioId);
    }
}
