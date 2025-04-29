package condorcet.Services;

import condorcet.DataAccessObjects.UserDAO;
import condorcet.Interfaces.DAO;
import condorcet.Interfaces.Service;
import condorcet.Models.Entities.Passenger;
import condorcet.Models.Entities.PersonData;
import condorcet.Models.Entities.User;
import condorcet.Models.Entities.UserMark;

import java.util.List;

public class UserService implements Service<User> {
    DAO daoService = new UserDAO();
    @Override
    public User findEntity(int id) {
        User entity = (User) daoService.findById(id);
        if (entity.getPersonData() != null) {
            for (Passenger passenger : entity.getPersonData().getPassengers()) {
                passenger.setPersonData(null);
                passenger.getFlight().setUserMarks(null);
                passenger.getFlight().setAircraft(null);
                passenger.getFlight().setRoute(null);
            }
            entity.getPersonData().setUsers(null);
        }
        for (UserMark userMark : entity.getUserMarks()) {
            userMark.getFlight().setUserMarks(null);
            userMark.getFlight().setRoute(null);
            userMark.getFlight().setAircraft(null);
            User tempUser = new User();
            tempUser.setId(userMark.getUser().getId());
            tempUser.setPassword(userMark.getUser().getPassword());
            tempUser.setName(userMark.getUser().getName());
            tempUser.setLogin(userMark.getUser().getLogin());
            tempUser.setRole(userMark.getUser().getRole());
            userMark.setUser(tempUser);
        }
        return entity;
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

    @Override
    public List<User> findAllEntities() {
        return daoService.findAll();
    }
}
