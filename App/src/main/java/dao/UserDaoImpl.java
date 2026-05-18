package dao;

import entity.User;
import exception.DuplicateEntityException;
import exception.EntityNotFoundException;
import exception.ValidationException;
import util.HibernateUtil;
import org.hibernate.*;
import org.hibernate.exception.ConstraintViolationException;
import org.apache.log4j.Logger;

import java.util.List;
import java.util.Optional;

public class UserDaoImpl implements UserDao {

    private static final Logger log = Logger.getLogger(UserDaoImpl.class);

    @Override
    public void create(User user) {
        Transaction tx = null;

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {

            tx = session.beginTransaction();
            session.save(user);
            tx.commit();

        } catch (ConstraintViolationException e) {
            rollback(tx);
            throw new DuplicateEntityException("Пользователь с таким email уже существует");

        } catch (HibernateException e) {
            rollback(tx);
            throw new ValidationException("Ошибка при создании пользователя");
        }
    }

    @Override
    public Optional<User> findById(Long id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return Optional.ofNullable(session.get(User.class, id));
        } catch (HibernateException e) {
            throw new ValidationException("Ошибка при поиске пользователя");
        }
    }

    @Override
    public List<User> findAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("from User", User.class).list();
        } catch (HibernateException e) {
            throw new ValidationException("Ошибка при получении списка пользователей");
        }
    }

    @Override
    public void update(User user) {
        Transaction tx = null;

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {

            tx = session.beginTransaction();
            session.update(user);
            tx.commit();

        } catch (ConstraintViolationException e) {
            rollback(tx);
            throw new DuplicateEntityException("Пользователь с таким email уже существует");

        } catch (HibernateException e) {
            rollback(tx);
            throw new ValidationException("Ошибка при обновлении пользователя");
        }
    }

    @Override
    public void delete(Long id) {
        Transaction tx = null;

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {

            tx = session.beginTransaction();
            User user = session.get(User.class, id);

            if (user == null) {
                throw new EntityNotFoundException("Пользователь не найден");
            }

            session.delete(user);
            tx.commit();

        } catch (HibernateException e) {
            rollback(tx);
            throw new ValidationException("Ошибка при удалении пользователя");
        }
    }

    private void rollback(Transaction tx) {
        if (tx != null) {
            try { tx.rollback(); } catch (Exception ignored) {}
        }
    }
}