package ru.otus.homework.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document
public class User {

    @Id
    private String id;

    private String name;

    private String password;

    private String role;

    private Boolean accountNonLocked;

    private LocalDateTime lastSuccessLogin;

    public User(String name, String password, Boolean accountNonLocked) {
        this(name, password, "USER", accountNonLocked);
    }

    public User(String name, String password, String role, Boolean accountNonLocked) {
        this.name = name;
        this.password = password;
        this.accountNonLocked = accountNonLocked;
        this.role = role;
    }

}
