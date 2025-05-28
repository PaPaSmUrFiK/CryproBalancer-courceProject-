package cryptoBalancer.DataAccessObjects;

import cryptoBalancer.Models.Entities.CryptoHistory;
import cryptoBalancer.Utility.HibernateSessionFactory;
import org.hibernate.Session;

import java.util.List;

public class CryptoHistoryDAO extends BaseDAO<CryptoHistory> {
    @Override
    public void save(CryptoHistory cryptoHistory) {
        executeInTransaction(session -> session.persist(cryptoHistory));
    }

    @Override
    public void update(CryptoHistory cryptoHistory) {
        executeInTransaction(session -> session.merge(cryptoHistory));
    }

    @Override
    public CryptoHistory findById(int id) {
        try (Session session = HibernateSessionFactory.getSessionFactory().openSession()) {
            return session.get(CryptoHistory.class, id);
        }
    }

    @Override
    public void delete(CryptoHistory cryptoHistory) {
        executeInTransaction(session -> session.remove(cryptoHistory));
    }

    @Override
    public List<CryptoHistory> findAll() {
        try (Session session = HibernateSessionFactory.getSessionFactory().openSession()) {
            return session.createQuery("FROM CryptoHistory", CryptoHistory.class).getResultList();
        }
    }

    public List<CryptoHistory> getLastNRecords(int cryptoId, int n) {
        try (Session session = HibernateSessionFactory.getSessionFactory().openSession()) {
            return session.createQuery(
                            "FROM CryptoHistory ch WHERE ch.crypto.cryptoId = :cryptoId " +
                            "AND ch.price IS NOT NULL " +
                            "ORDER BY ch.dateChanged DESC",
                            CryptoHistory.class
                    )
                    .setParameter("cryptoId", cryptoId)
                    .setMaxResults(n)
                    .getResultList();
        }
    }
}