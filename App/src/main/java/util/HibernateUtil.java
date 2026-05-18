package util;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.apache.log4j.Logger;

public class HibernateUtil {

    private static final Logger log = Logger.getLogger(HibernateUtil.class);

    private static final SessionFactory sessionFactory = buildSessionFactory();

    private static SessionFactory buildSessionFactory() {
        try {
            log.info("Инициализация Hibernate...");
            return new Configuration().configure().buildSessionFactory();
        } catch (Exception e) {
            log.error("Ошибка инициализации Hibernate", e);
            throw new RuntimeException("Ошибка инициализации Hibernate", e);
        }
    }

    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }
}