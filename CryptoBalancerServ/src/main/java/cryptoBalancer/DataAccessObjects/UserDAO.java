package cryptoBalancer.DataAccessObjects;

import cryptoBalancer.Models.Entities.User;
import cryptoBalancer.Utility.HibernateSessionFactory;
import org.hibernate.Session;
import java.util.List;

public class UserDAO extends BaseDAO<User> {
    @Override
    public void save(User user) {
        executeInTransaction(session -> session.persist(user));
    }

    @Override
    public void update(User user) {
        executeInTransaction(session -> session.merge(user));
    }

    @Override
    public User findById(int id) {
        try (Session session = HibernateSessionFactory.getSessionFactory().openSession()) {
            return session.get(User.class, id);
        }
    }

    @Override
    public void delete(User user) {
        executeInTransaction(session -> session.remove(user));
    }

    @Override
    public List<User> findAll() {
        try (Session session = HibernateSessionFactory.getSessionFactory().openSession()) {
            return session.createQuery("FROM User", User.class).getResultList();
        }
    }
}
