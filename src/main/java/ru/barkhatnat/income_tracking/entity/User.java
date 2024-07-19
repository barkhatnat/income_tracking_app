package ru.barkhatnat.income_tracking.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UuidGenerator;

import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;


@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "users")
public class User {

    @Id
    @UuidGenerator
    private UUID id;

    @Column
    @NotNull
    @Size(min = 1, max = 64)
    private String username;

    @Column
    @NotNull
    @Size(min = 1, max = 256)
    private String password;

    @Column
    @NotNull
    @Size(min = 1, max = 128)
    private String email;

    @CreationTimestamp
    @Column
    @NotNull
    private Timestamp createdAt;

    @Column
    @NotNull
    private String role;

    @OneToMany(mappedBy = "user")
    private List<Account> accounts;

    @OneToMany(mappedBy = "user")
    private List<Category> categories;

    public User(String username, String password, String email, Timestamp createdAt, String role) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.createdAt = createdAt;
        this.role = role;
    }
}
