package cryptoBalancer.Utility;

import cryptoBalancer.Models.Entities.*;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;

public class HibernateSessionFactory {
    private static SessionFactory sessionFactory; //Работает только в Hibernate
    //EntityManager описан в JPA и доступен в любой реализации ORM
    private static final String CONFIG_FILE_NAME = "hibernate.cfg.xml";

    private HibernateSessionFactory() {
    }

    public static SessionFactory getSessionFactory() {
        if (sessionFactory == null) {
            try{
                Configuration configuration = new Configuration().configure();
                configuration.addAnnotatedClass(Analytic.class);
                configuration.addAnnotatedClass(Crypto.class);
                configuration.addAnnotatedClass(CryptoHistory.class);
                configuration.addAnnotatedClass(Investment.class);
                configuration.addAnnotatedClass(Portfolio.class);
                configuration.addAnnotatedClass(Role.class);
                configuration.addAnnotatedClass(User.class);
                StandardServiceRegistryBuilder builder = new StandardServiceRegistryBuilder().configure(CONFIG_FILE_NAME);
                sessionFactory = configuration.buildSessionFactory(builder.build());
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException("Ошибка создания SessionFactory: " + e.getMessage(), e);
            }
        }
        return sessionFactory;
    }
}
