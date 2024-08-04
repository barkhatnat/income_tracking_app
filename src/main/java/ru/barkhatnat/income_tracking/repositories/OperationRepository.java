package ru.barkhatnat.income_tracking.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.barkhatnat.income_tracking.entity.Category;
import ru.barkhatnat.income_tracking.entity.Operation;

import java.util.List;
import java.util.UUID;

@Repository
public interface OperationRepository extends JpaRepository<Operation, UUID> {
    List<Operation> findOperationsByAccountId(UUID account_id);

    @Modifying
    @Query("UPDATE Operation o SET o.category = :defaultCategory WHERE o.category = :oldCategory")
    void updateCategory(@Param("oldCategory") Category oldCategory, @Param("defaultCategory") Category defaultCategory);
}
