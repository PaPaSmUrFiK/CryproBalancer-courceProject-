package cryptoBalancer.DataAccessObjects;

import cryptoBalancer.Models.Entities.Role;
import cryptoBalancer.Utility.HibernateSessionFactory;
import org.hibernate.Session;
import java.util.List;

public class RoleDAO extends BaseDAO<Role> {
    @Override
    public void save(Role role){
        executeInTransaction(session -> session.persist(role));
    }

    @Override
    public void update(Role role){
        executeInTransaction(session -> session.merge(role));
    }

    @Override
    public Role findById(int id) {
        try (Session session = HibernateSessionFactory.getSessionFactory().openSession()) {
            return session.get(Role.class, id);
        }
    }

    @Override
    public void delete(Role role){
        executeInTransaction(session -> session.remove(role));
    }

    @Override
    public List<Role> findAll(){
        try (Session session = HibernateSessionFactory.getSessionFactory().openSession()) {
            return session.createQuery("FROM Role", Role.class).getResultList();
        }
    }
}
