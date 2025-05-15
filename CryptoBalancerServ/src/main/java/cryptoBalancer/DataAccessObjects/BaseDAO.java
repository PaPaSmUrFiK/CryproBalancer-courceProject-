package cryptoBalancer.DataAccessObjects;

import cryptoBalancer.Interfaces.DAO;
import cryptoBalancer.Utility.HibernateSessionFactory;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.function.Consumer;

public abstract class BaseDAO<T> implements DAO<T> {
    protected void executeInTransaction(Consumer<Session> operation) {
        Transaction transaction = null;
        try (Session session = HibernateSessionFactory.getSessionFactory().openSession()) {

            transaction = session.beginTransaction();
            operation.accept(session);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null && transaction.isActive()) transaction.rollback();
            throw new RuntimeException("Transaction failed for " + getClass().getSimpleName(), e);
        }
    }
}
