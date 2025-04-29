package condorcet.DataAccessObjects;

import condorcet.Interfaces.DAO;
import condorcet.Models.Entities.User;
import condorcet.Utility.HibernateSessionFactory;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;

public class UserDAO implements DAO {
    @Override
    public void save(Object obj) {
        Session session = HibernateSessionFactory.getSessionFactory().openSession();
        Transaction tx1 = session.beginTransaction();
        session.save(obj);
        tx1.commit();
        session.close();
    }

    @Override
    public void update(Object obj) {
        Session session = HibernateSessionFactory.getSessionFactory().openSession();
        Transaction tx1 = session.beginTransaction();
        session.saveOrUpdate(obj);
        tx1.commit();
        session.close();
    }

    @Override
    public void delete(Object obj) {
        Session session = HibernateSessionFactory.getSessionFactory().openSession();
        Transaction tx1 = session.beginTransaction();
        session.delete(obj);
        tx1.commit();
        session.close();
    }

    @Override
    public Object findById(int id) {
        Session session = HibernateSessionFactory.getSessionFactory().openSession();
        User user = session.get(User.class, id);
        session.close();
        return user;
    }

    @Override
    public List findAll() {
        Session session =   HibernateSessionFactory.getSessionFactory().openSession();
        List<Object> users = (List<Object>)session.createQuery("From User").list();
        session.close();
        return users;
    }
}
