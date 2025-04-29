package cryptoBalancer.DataAccessObjects;

import cryptoBalancer.Models.Entities.Portfolio;
import cryptoBalancer.Utility.HibernateSessionFactory;
import org.hibernate.Session;
import java.util.List;

public class PortfolioDAO extends BaseDAO<Portfolio> {
    @Override
    public void save(Portfolio portfolio){
        executeInTransaction(session -> session.persist(portfolio));
    }

    @Override
    public void update(Portfolio portfolio){
        executeInTransaction(session -> session.merge(portfolio));
    }

    @Override
    public Portfolio findById(int id) {
        try (Session session = HibernateSessionFactory.getSessionFactory().openSession()) {
            return session.get(Portfolio.class, id);
        }
    }

    @Override
    public void delete(Portfolio portfolio){
        executeInTransaction(session -> session.remove(portfolio));
    }

    @Override
    public List<Portfolio> findAll(){
        try (Session session = HibernateSessionFactory.getSessionFactory().openSession()) {
            return session.createQuery("FROM Portfolio ", Portfolio.class).getResultList();
        }
    }
}
