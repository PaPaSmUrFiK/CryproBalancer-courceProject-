package cryptoBalancer.Services;

import cryptoBalancer.DataAccessObjects.RoleDAO;
import cryptoBalancer.Interfaces.DAO;
import cryptoBalancer.Interfaces.Service;
import cryptoBalancer.Models.Entities.Role;
import cryptoBalancer.Models.Entities.User;

import java.util.List;

public class RoleService implements Service<Role> {
    DAO<Role> daoService = new RoleDAO();

    @Override
    public Role findEntity(int id) {
        return daoService.findById(id);
    }

    @Override
    public void saveEntity(Role entity) {
        daoService.save(entity);
    }

    @Override
    public void deleteEntity(Role entity) {
        daoService.delete(entity);
    }

    @Override
    public void updateEntity(Role entity) {
        daoService.update(entity);
    }

    public List<Role> findAllEntities() {
        return daoService.findAll();
    }

}
