package cryptoBalancer.DataAccessObjects;

import cryptoBalancer.Models.Entities.Crypto;
import cryptoBalancer.Utility.HibernateSessionFactory;
import org.hibernate.Session;

import java.util.List;

public class CryptoDAO extends BaseDAO<Crypto> {
    @Override
    public void save(Crypto crypto){
        executeInTransaction(session -> session.persist(crypto));
    }

    @Override
    public void update(Crypto crypto){
        executeInTransaction(session -> session.merge(crypto));
    }

    @Override
    public Crypto findById(int id) {
        try (Session session = HibernateSessionFactory.getSessionFactory().openSession()) {
            return session.get(Crypto.class, id);
        }
    }

    @Override
    public void delete(Crypto crypto){
        executeInTransaction(session -> session.remove(crypto));
    }

    @Override
    public List<Crypto> findAll(){
        try (Session session = HibernateSessionFactory.getSessionFactory().openSession()) {
            return session.createQuery("FROM Crypto", Crypto.class).getResultList();
        }
    }

}
