package org.hiberproject.entity;

import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import lombok.*;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.*;

@Data                                                                           // Это POJO сущность (геттеры, сеттеры, хеш-код, эквалс, ту-стринг, конструкторы и т.д.)
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "username")
@ToString(exclude = {"company", "profile"})
@Builder                                                                        // для SessionFactory чтобы при вызове сессии добавлять в этот класс нужные данные для записи в БД
@Entity                                                                         // каждая сущность в hibernate должна иметь PK
@Table(name = "users", schema = "public")                                       // чтобы название таблицы совпадала с названием сущности (DB 'user' = .class 'user'), а не названием класса User
@TypeDef(name = "hiberproject", typeClass = JsonBinaryType.class)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @AttributeOverride(name = "birthDate", column = @Column(name = "birth_date"))
    private PersonalInfo personalInfo;

    @Column(unique = true)
    private String username;

    @Type(type = "hiberproject")                                               // для Json формата конвертер
    private String info;

    @Enumerated(EnumType.STRING)
    private Role role;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id") // company_id
    private Company company;

    @OneToOne(
            mappedBy = "user",
            cascade = CascadeType.ALL,
            fetch = FetchType.LAZY,
            optional = false
    )
    private Profile profile;
}
