package condorcet.Utility;

import condorcet.Models.Entities.*;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;

public class HibernateSessionFactory {
    private static SessionFactory sessionFactory;
    private static String configFileName = "hibernate.cfg.xml";

    private HibernateSessionFactory() {}

    public static SessionFactory getSessionFactory() {
        if (sessionFactory == null) {
            try {
                Configuration configuration = new Configuration().configure();
                configuration.addAnnotatedClass(Route.class);
                configuration.addAnnotatedClass(PersonData.class);
                configuration.addAnnotatedClass(User.class);
                configuration.addAnnotatedClass(UserMark.class);
                configuration.addAnnotatedClass(Passenger.class);
                configuration.addAnnotatedClass(Aircraft.class);
                configuration.addAnnotatedClass(Flight.class);
                StandardServiceRegistryBuilder builder = new StandardServiceRegistryBuilder().configure(configFileName);
                sessionFactory = configuration.buildSessionFactory(builder.build());

            } catch (Exception e) {
                System.out.println("Исключение!" + e);
            }
        }
        return sessionFactory;
    }
}
