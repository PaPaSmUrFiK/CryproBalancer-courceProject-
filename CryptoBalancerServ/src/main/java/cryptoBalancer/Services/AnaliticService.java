package cryptoBalancer.Services;

import cryptoBalancer.DataAccessObjects.AnaliticDAO;
import cryptoBalancer.Interfaces.DAO;
import cryptoBalancer.Interfaces.Service;
import cryptoBalancer.Models.Entities.Analytic;

import java.util.List;

public class AnaliticService implements Service<Analytic> {
    DAO<Analytic> daoService = new AnaliticDAO();

    @Override
    public Analytic findEntity(int id) {
        return daoService.findById(id);
    }

    @Override
    public void saveEntity(Analytic entity) {
        daoService.save(entity);
    }

    @Override
    public void deleteEntity(Analytic entity) {
        daoService.delete(entity);
    }

    @Override
    public void updateEntity(Analytic entity) {
        daoService.update(entity);
    }

    public List<Analytic> findAllEntities() {
        return daoService.findAll();
    }
}
