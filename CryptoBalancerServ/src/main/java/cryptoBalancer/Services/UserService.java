package cryptoBalancer.Services;

import cryptoBalancer.DataAccessObjects.UserDAO;
import cryptoBalancer.Interfaces.DAO;
import cryptoBalancer.Interfaces.Service;
import cryptoBalancer.Models.Entities.Role;
import cryptoBalancer.Models.Entities.User;

import java.util.List;

public class UserService implements Service<User> {
    DAO<User> daoService = new UserDAO();
    RoleService roleService = new RoleService();

    @Override
    public User findEntity(int id) {
        return daoService.findById(id);
    }

    @Override
    public void saveEntity(User entity) {
        // Валидация данных
        validateUser(entity);

        // Проверка уникальности логина и email
        List<User> users = findAllEntities();
        if (users == null) {
            throw new IllegalStateException("Ошибка при получении списка пользователей.");
        }

        if (users.stream().anyMatch(u -> u.getUsername().equals(entity.getUsername()))) {
            throw new IllegalArgumentException("Такой пользователь уже существует.");
        }

        if (users.stream().anyMatch(u -> u.getEmail().equals(entity.getEmail()))) {
            throw new IllegalArgumentException("Email уже используется.");
        }

        // Поиск роли "ROLE_USER"
        Role userRole = roleService.findByName("Пользователь");
        if (userRole == null) {
            throw new IllegalStateException("Ошибка работы базы данных");
        }

        // Присвоение роли пользователю
        entity.setRole(userRole);
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

    private void validateUser(User user) {
        if (user == null || user.getUsername() == null || user.getUsername().trim().isEmpty() ||
                user.getPasswordHash() == null || user.getPasswordHash().trim().isEmpty() ||
                user.getEmail() == null || user.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("Все поля обязательны для заполнения.");
        }
    }
}
