package cryptoBalancer.DataAccessObjects;

import cryptoBalancer.Models.Entities.Investment;
import cryptoBalancer.Utility.HibernateSessionFactory;
import org.hibernate.Session;

import java.util.List;

public class InvestmentDAO extends BaseDAO<Investment> {
    @Override
    public void save(Investment investment){
        executeInTransaction(session -> session.persist(investment));
    }

    @Override
    public void update(Investment investment){
        executeInTransaction(session -> session.merge(investment));
    }

    @Override
    public Investment findById(int id) {
        try (Session session = HibernateSessionFactory.getSessionFactory().openSession()) {
            return session.get(Investment.class, id);
        }
    }

    @Override
    public void delete(Investment investment){
        executeInTransaction(session -> session.remove(investment));
    }

    @Override
    public List<Investment> findAll(){
        try (Session session = HibernateSessionFactory.getSessionFactory().openSession()) {
            return session.createQuery("FROM Investment ", Investment.class).getResultList();
        }
    }
}
