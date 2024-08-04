package ru.barkhatnat.income_tracking.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.barkhatnat.income_tracking.entity.Account;
import ru.barkhatnat.income_tracking.entity.Category;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Repository
public interface AccountRepository extends JpaRepository<Account, UUID> {
    List<Account> findAccountsByUserId(UUID userId);

    @Modifying
    @Query("UPDATE Account a SET a.balance = :newBalance WHERE a.id = :accountId")
    void updateAccountBalance(@Param("accountId") UUID accountId, @Param("newBalance") BigDecimal newBalance);
}
