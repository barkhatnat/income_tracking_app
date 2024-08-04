package ru.barkhatnat.income_tracking.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table
public class Account {

    @Id
    @UuidGenerator
    private UUID id;

    @Column
    @NotBlank
    @Size(min = 1, max = 32)
    private String title;

    @Column
    @NotNull
    @DecimalMin(value = "-9999999999.99")
    @DecimalMax(value = "9999999999.99")
    private BigDecimal balance;

    @ManyToOne(cascade = {CascadeType.DETACH, CascadeType.REFRESH, CascadeType.MERGE, CascadeType.PERSIST})
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore
    private User user;

    @Column
    @NotNull
    private Timestamp createdAt;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "account", orphanRemoval = true)
    private List<Operation> operations;

    public Account(String title, BigDecimal balance, User user, Timestamp createdAt) {
        this.title = title;
        this.balance = balance;
        this.user = user;
        this.createdAt = createdAt;
    }

    public Account(UUID id, String title, BigDecimal balance, User user, Timestamp createdAt) {
        this.id = id;
        this.title = title;
        this.balance = balance;
        this.user = user;
        this.createdAt = createdAt;
    }
}
