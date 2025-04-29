package cryptoBalancer.Services;

import cryptoBalancer.DataAccessObjects.UserDAO;
import cryptoBalancer.Interfaces.DAO;
import cryptoBalancer.Interfaces.Service;
import cryptoBalancer.Models.Entities.User;

import java.util.List;

public class UserService implements Service<User> {
    DAO<User> daoService = new UserDAO();

    @Override
    public User findEntity(int id) {
        return daoService.findById(id);
    }

    @Override
    public void saveEntity(User entity) {
        daoService.save(entity);
    }

    @Override
    public void deleteEntity(User entity) {
        daoService.delete(entity);
    }

    @Override
    public void updateEntity(User entity) {
        daoService.update(entity);
    }

    public List<User> findAllEntities() {
        return daoService.findAll();
    }
}
