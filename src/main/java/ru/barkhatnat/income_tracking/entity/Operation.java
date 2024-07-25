package ru.barkhatnat.income_tracking.entity;

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
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table
public class Operation {

    @Id
    @UuidGenerator
    private UUID id;

    @Column
    @NotNull
    @DecimalMin(value = "-9999999999.99")
    @DecimalMax(value = "9999999999.99")
    private BigDecimal amount;

    @Column
    @NotNull
    private Timestamp datePurchase;

    @Column
    @Size(min = 1, max = 512)
    private String note;

    @ManyToOne(cascade = {CascadeType.DETACH, CascadeType.REFRESH, CascadeType.MERGE, CascadeType.PERSIST})
    @JoinColumn(name = "category_id")
    private Category category;

    @ManyToOne(cascade = {CascadeType.DETACH, CascadeType.REFRESH, CascadeType.MERGE, CascadeType.PERSIST})
    @JoinColumn(name = "account_id")
    private Account account;

    @Column(name = "created_at")
    private Timestamp createdAt;

    public Operation(BigDecimal amount, Timestamp datePurchase, Category category, Account account, String note, Timestamp createdAt) {
        this.amount = amount;
        this.datePurchase = datePurchase;
        this.category = category;
        this.account = account;
        this.note = note;
        this.createdAt = createdAt;
    }

    public Operation(UUID id,BigDecimal amount, Timestamp datePurchase, Category category, Account account, String note, Timestamp createdAt) {
        this.id = id;
        this.amount = amount;
        this.datePurchase = datePurchase;
        this.category = category;
        this.account = account;
        this.note = note;
        this.createdAt = createdAt;
    }
}
