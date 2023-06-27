package org.hiberproject;

import lombok.Cleanup;
import org.hibernate.FlushMode;
import org.hibernate.Hibernate;
import org.hibernate.annotations.QueryHints;
import org.hiberproject.entity.*;
import org.hiberproject.util.HibernateTestUtil;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

// 6. Класс Session

class HibernateRunnerTest {

    @Test
    void checkHql() {
        try (var sessionFactory = HibernateTestUtil.buildSessionFactory();
             var session = sessionFactory.openSession()) {
            session.beginTransaction();

//            HQL / JPQL
//            select u.* from users u where u.firstname = 'Ivan'
            String name = "Ivan";
            var result = session.createNamedQuery(
//                    "select u from User u where u.personalInfo.firstname = ?1", User.class)
                            "findUserByName", User.class)
//                    .setParameter(1, name)
                    .setParameter("firstname", name)
                    .setParameter("companyName", "Google")
                    .setFlushMode(FlushMode.COMMIT)
                    .setHint(QueryHints.FETCH_SIZE, "50")
                    .list();

            var countRows = session.createQuery("update User u set u.role = 'ADMIN'")
                            .executeUpdate();

            session.createNativeQuery("select u.* from users u where u.firstname = 'Ivan'", User.class);

            session.getTransaction().commit();
        }
    }

    @Test
    void checkH2() {
        try (var sessionFactory = HibernateTestUtil.buildSessionFactory();
             var session = sessionFactory.openSession()) {
            session.beginTransaction();

            var google = Company.builder()
                    .name("Google")
                    .build();
            session.save(google);

//            Programmer programmer = Programmer.builder()
//                    .username("ivan_tret@gmail.com")
//                    .language(Language.Java)
//                    .company(google)
//                    .build();
//            session.save(programmer);
//
//            Manager manager = Manager.builder()
//                    .username("daria_tret@gmail.com")
//                    .projectName("Starter")
//                    .company(google)
//                    .build();
//            session.save(manager);
            session.flush();

            session.clear();

//            var programmer1 = session.get(Programmer.class, 1L);
            var manager1 = session.get(User.class, 1L);
            System.out.println();

            session.getTransaction().commit();
        }
    }

    @Test
    void localeInfo() {
        try (var sessionFactory = HibernateTestUtil.buildSessionFactory();
             var session = sessionFactory.openSession()) {
            session.beginTransaction();

            var company = session.get(Company.class, 2);
//            company.getLocales().add(LocaleInfo.of("ru", "Описание на русском"));
//            company.getLocales().add(LocaleInfo.of("en", "English description"));
//            System.out.println(company.getLocales());
//            company.getUsers().forEach(System.out::println);
            company.getUsers().forEach((k, v) -> System.out.println(v));

            session.getTransaction().commit();
        }
    }

    @Test
    void checkManyToMany() {
        try (var sessionFactory = HibernateTestUtil.buildSessionFactory();
             var session = sessionFactory.openSession()) {
            session.beginTransaction();

            var user = session.get(User.class, 1L);
            var chat = session.get(Chat.class, 1L);

//            var userChat = UserChat.builder()
//                    .createdAt(Instant.now())
//                    .createdBy(user.getUsername())
//                    .build();
//            userChat.setUser(user);
//            userChat.setChat(chat);

//            session.save(userChat);

//            user.getChats().clear();

//            var chat = Chat.builder()
//                    .name("hiberproject")
//                    .build();
//            user.addChat(chat);
//
//            session.save(chat);

            session.getTransaction().commit();
        }
    }

    @Test
    void checkOneToOne() {
        try (var sessionFactory = HibernateTestUtil.buildSessionFactory();
             var session = sessionFactory.openSession()) {
            session.beginTransaction();

//            var user = User.builder()
//                    .username("test3@gmail.com")
//                    .build();

            var profile = Profile.builder()
                    .language("ru")
                    .street("Kolasa 18")
                    .build();

//            profile.setUser(user);
//
//            session.save(user);
//            profile.setUser(user);
//            session.save(profile);

            session.getTransaction().commit();
        }
    }

    @Test
    void checkOrhanRemoval(){
        try (var sessionFactory = HibernateTestUtil.buildSessionFactory();
             var session = sessionFactory.openSession()) {
            session.beginTransaction();

            Company company = session.getReference(Company.class, 10);
//            company.getUsers().removeIf(user -> user.getId().equals(8L));

            session.getTransaction().commit();
        }
    }

    @Test
    void checkLazyInitialisation() {
        Company company = null;
        try (var sessionFactory = HibernateTestUtil.buildSessionFactory();
             var session = sessionFactory.openSession()) {
            session.beginTransaction();

            company = session.getReference(Company.class, 2);

            session.getTransaction().commit();
        }
        var users = company.getUsers();
        System.out.println(users.size());
    }

    @Test
    void getCompanyById() {
        @Cleanup var sessionFactory = HibernateTestUtil.buildSessionFactory();
        @Cleanup var session = sessionFactory.openSession();
        session.beginTransaction();

        var company = session.get(Company.class, 2);
        Hibernate.initialize(company.getUsers());
        System.out.println();

        session.getTransaction().commit();
    }

    @Test
    void deleteCompany() {
        @Cleanup var sessionFactory = HibernateTestUtil.buildSessionFactory();
        @Cleanup var session = sessionFactory.openSession();
        session.beginTransaction();

        var company = session.get(Company.class, 9);
//        session.delete(company);

        session.getTransaction().commit();
    }

    @Test
    void deleteUser() {
        @Cleanup var sessionFactory = HibernateTestUtil.buildSessionFactory();
        @Cleanup var session = sessionFactory.openSession();
        session.beginTransaction();

        var user = session.get(User.class, 9L);
//        session.delete(user);

        session.getTransaction().commit();
    }

    @Test
    void addUserToCompany() {
        @Cleanup var sessionFactory = HibernateTestUtil.buildSessionFactory();
        @Cleanup var session = sessionFactory.openSession();
        session.beginTransaction();

        var company = Company.builder()
                .name("Facebook")
                .build();

//        var user = User.builder()
//                .username("SvetaNew@gmail.com")
//                .build();
//        company.addUser(user);

        session.save(company);

        session.getTransaction().commit();
    }

    @Test
    void oneToMany() {
        @Cleanup var sessionFactory = HibernateTestUtil.buildSessionFactory();
        @Cleanup var session = sessionFactory.openSession();
        session.beginTransaction();

        var company = session.get(Company.class, 2);
        Hibernate.initialize(company.getUsers());
        System.out.println(company.getUsers());

        session.getTransaction().commit();
    }

    @Test
    void checkGetReflectionApi() throws SQLException, NoSuchMethodException,
            InvocationTargetException, InstantiationException,
            IllegalAccessException, NoSuchFieldException {

        PreparedStatement preparedStatement = null;
        ResultSet resultSet = preparedStatement.executeQuery();
        resultSet.getString("username");
        resultSet.getString("lastname");
        resultSet.getString("lastname");

        Class<User> clazz = User.class;

        Constructor<User> constructor = clazz.getConstructor();
        User user = constructor.newInstance();
        Field usernameField = clazz.getDeclaredField("username");
        usernameField.setAccessible(true);
        usernameField.set(user, resultSet.getString("username"));
    }

    @Test
    void checkReflectionApi() throws SQLException, IllegalAccessException {
//        User user = User.builder()
//                .build();

        String sql = """
                insert
                into
                %s
                (%s)
                values
                (%s)
                """;
//        String tableName = ofNullable(user.getClass().getAnnotation(Table.class))
//                .map(tableAnnotation -> tableAnnotation.schema() + "." + tableAnnotation.name())
//                .orElse(user.getClass().getName());
//
//        Field[] declaredFields = user.getClass().getDeclaredFields();

//        String columnNames = Arrays.stream(declaredFields)
//                .map(field -> ofNullable(field.getAnnotation(Column.class))
//                        .map(Column::name)
//                        .orElse(field.getName()))
//                .collect(joining(", "));
//
//        String columnValues = Arrays.stream(declaredFields)
//                .map(field -> "?")
//                .collect(joining(", "));
//
//        System.out.println(sql.formatted(tableName, columnNames, columnValues));
//
//        Connection connection = null;
//        PreparedStatement preparedStatement = connection.prepareStatement(sql.formatted(tableName, columnNames, columnValues));
//        for (Field declaredField : declaredFields) {
//            declaredField.setAccessible(true);
//            preparedStatement.setObject(1, declaredField.get(user));
//        }
    }
}
