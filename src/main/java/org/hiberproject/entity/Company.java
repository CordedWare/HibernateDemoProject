package org.hiberproject.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.SortNatural;

import javax.persistence.*;
import java.util.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "name")
@ToString(exclude = "users")
@Builder
@Entity
public class Company {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true)
    private String name;

    @Builder.Default
    @OneToMany(mappedBy = "company", cascade = CascadeType.ALL, orphanRemoval = true)
//    @OrderBy("username DESC, personalInfo.lastname ASC")
//    @OrderBy("personalInfo.firstname")
//    @OrderColumn(name = "id")
    @SortNatural
    @MapKey(name = "username")
    private Map<String, User> users = new TreeMap<>();

    @Builder.Default
    @ElementCollection
    @CollectionTable(name = "company_locale", joinColumns = @JoinColumn(name = "company_id"))
//    @AttributeOverride(name = "lang", column =  @Column(name = "language"))
//    private List<LocaleInfo> locales = new ArrayList<>();
//    @Column(name = "description")
    @MapKeyColumn(name = "lang")
    private Map<String, String> locales = new HashMap();


    public void addUser(User user) {
        users.put(user.getUsername(), user);
        user.setCompany(this);
    }
}
