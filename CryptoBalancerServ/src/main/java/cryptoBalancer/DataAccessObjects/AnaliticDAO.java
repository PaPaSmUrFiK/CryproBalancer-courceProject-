package cryptoBalancer.DataAccessObjects;

import cryptoBalancer.Models.Entities.Analytic;
import cryptoBalancer.Utility.HibernateSessionFactory;
import org.hibernate.Session;

import java.util.List;

public class AnaliticDAO extends BaseDAO<Analytic> {
    @Override
    public void save(Analytic analytic){
        executeInTransaction(session -> session.persist(analytic));
    }

    @Override
    public void update(Analytic analytic){
        executeInTransaction(session -> session.merge(analytic));
    }

    @Override
    public Analytic findById(int id) {
        try (Session session = HibernateSessionFactory.getSessionFactory().openSession()) {
            return session.get(Analytic.class, id);
        }
    }

    @Override
    public void delete(Analytic analytic){
        executeInTransaction(session -> session.remove(analytic));
    }

    @Override
    public List<Analytic> findAll(){
        try (Session session = HibernateSessionFactory.getSessionFactory().openSession()) {
            return session.createQuery("FROM Analytic ", Analytic.class).getResultList();
        }
    }
}
