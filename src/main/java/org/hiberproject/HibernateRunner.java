package org.hiberproject;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.Hibernate;
import org.hiberproject.entity.Birthday;
import org.hiberproject.entity.Company;
import org.hiberproject.entity.PersonalInfo;
import org.hiberproject.entity.User;
import org.hiberproject.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.sql.SQLException;
import java.time.LocalDate;

@Slf4j
public class HibernateRunner {

    public static void main(String[] args) throws SQLException {
        Company company = Company.builder()
                .name("Amazon")
                .build();
        User user = null;
//                User.builder() // Transient состояние
//                .username("ivan@gmail.com")
//                .personalInfo(PersonalInfo.builder()
//                        .lastname("Petrov")
//                        .firstname("Petr")
//                        .birthDate(new Birthday(LocalDate.of(2000, 1, 2)))
//                        .build())
//                .company(company)
//                .build();

        try (SessionFactory sessionFactory = HibernateUtil.buildSessionFactory()) { // Persistent состояние
            Session session1 = sessionFactory.openSession();
            try (session1) {
                Transaction transaction = session1.beginTransaction();

                session1.save(user);

                session1.getTransaction().commit();
            }
        }
    }
}