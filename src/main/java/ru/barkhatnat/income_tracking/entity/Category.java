package ru.barkhatnat.income_tracking.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table
public class Category {
    @Id
    @UuidGenerator
    private UUID id;

    @Column
    @NotNull
    @Size(min = 1, max = 32)
    private String title;

    @Column
    @NotNull
    private Boolean categoryType;

    @ManyToOne(cascade = {CascadeType.DETACH, CascadeType.REFRESH, CascadeType.MERGE, CascadeType.PERSIST})
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private User user;

    @OneToMany(mappedBy = "category")
    List<Operation> operations;

    public Category(String title, Boolean categoryType, User user) {
        this.title = title;
        this.categoryType = categoryType;
        this.user = user;
    }
}
