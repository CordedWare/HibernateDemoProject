package org.hiberproject.util;

import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import lombok.experimental.UtilityClass;

import org.hibernate.SessionFactory;
import org.hibernate.boot.model.naming.CamelCaseToUnderscoresNamingStrategy;
import org.hibernate.cfg.Configuration;
import org.hiberproject.converter.BirthdayConverter;
import org.hiberproject.entity.User;

@UtilityClass
public class HibernateUtil {

    public static SessionFactory buildSessionFactory() {
        Configuration configuration = buildConfiguration();
        configuration.configure();                                                                      // запускает настройки конфигурации и ищет путь к hibernate.cfg.xml.xml (connection)

        return configuration.buildSessionFactory();
    }

    public static Configuration buildConfiguration() {
        Configuration configuration = new Configuration();                                              // объявляем для настройки конфигурации
        configuration.setPhysicalNamingStrategy(new CamelCaseToUnderscoresNamingStrategy());            // для User, чтобы названия переменных совпадали с названиями полей в БД (если они отличаются). UPD: офф, т.к. стал юзать аннотацию конвертер
        configuration.addAnnotatedClass(User.class);
        configuration.addAttributeConverter(new BirthdayConverter());                                   // чтобы хибер автоматически эплаил конвертер и нам не приходилось прописывать его в Entity с аннотацией @Convert(converter = BirthdayConverter.class)
        configuration.registerTypeOverride(new JsonBinaryType());                                       // Подключили Json тип

        return configuration;
    }
}