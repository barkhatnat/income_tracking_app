package ru.barkhatnat.income_tracking.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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
    @NotNull
    @Size(min = 1, max = 32)
    private String title;

    @Column
    @NotNull
    @DecimalMin(value = "-9999999999.99")
    @DecimalMax(value = "9999999999.99")
    private BigDecimal balance;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private User user;

    @Column
    @NotNull
    private Timestamp createdAt;

    @OneToMany(mappedBy = "account")
    private List<Operation> operations;

    public Account(String title, BigDecimal balance, User user, Timestamp createdAt) {
        this.title = title;
        this.balance = balance;
        this.user = user;
        this.createdAt = createdAt;
    }
}
