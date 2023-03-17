package org.hiberproject;

import lombok.extern.slf4j.Slf4j;
import org.hiberproject.entity.PersonalInfo;
import org.hiberproject.entity.User;
import org.hiberproject.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.sql.SQLException;

@Slf4j
public class HibernateRunner {

    public static void main(String[] args) throws SQLException {
        User user = User.builder()                                                 // Transient состояние
                .username("petr11@gmail.com")
                .personalInfo(PersonalInfo.builder()
                        .lastname("Petrov")
                        .firstname("Petr")
                        .build())
                .build();
        log.info("User entity is in transient state object: {}", user);

        try (SessionFactory sessionFactory = HibernateUtil.buildSessionFactory()) { // Persistent состояние
            Session session1 = sessionFactory.openSession();
            try (session1) {
                Transaction transaction = session1.beginTransaction();
                log.trace("Transaction is created, {}", transaction);

                session1.saveOrUpdate(user);
                log.trace("User is in persistent state: {}, session {}", user, session1);

                session1.getTransaction().commit();
            }
            log.warn("User is in detached state: {}, session is closed {}", user, session1);
        } catch (Exception exception) {
            log.error("Exception occured", exception);
            throw exception;
        }
    }
}