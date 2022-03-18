package ru.otus.homework.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "sec_user")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "name")
    private String name;

    @Column(name = "password")
    private String password;

    @Column(name = "account_non_locked")
    private Boolean accountNonLocked;

    @Column(name = "login_attempts")
    private int loginAttempts;

    @Column(name = "first_attempt_time")
    private LocalDateTime firstAttempt;

    @Column(name = "last_success_login")
    private LocalDateTime lastSuccessLogin;

    @Fetch(FetchMode.SUBSELECT)
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "sec_user_role", joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "role_id"))
    private List<Role> roles;

    public User(String name, String password, Boolean accountNonLocked) {
        this.name = name;
        this.password = password;
        this.accountNonLocked = accountNonLocked;
    }

    public User(String name, String password, Boolean accountNonLocked, List<Role> roles) {
        this.name = name;
        this.password = password;
        this.accountNonLocked = accountNonLocked;
        this.roles = roles;
    }

}
