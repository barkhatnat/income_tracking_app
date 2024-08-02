package ru.barkhatnat.income_tracking.api.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import ru.barkhatnat.income_tracking.entity.Account;
import ru.barkhatnat.income_tracking.entity.Category;
import ru.barkhatnat.income_tracking.entity.Operation;
import ru.barkhatnat.income_tracking.entity.User;
import ru.barkhatnat.income_tracking.service.AccountService;
import ru.barkhatnat.income_tracking.service.BalanceServiceImpl;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.UUID;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class BalanceServiceTest {
    @Mock
    private AccountService accountService;
    @InjectMocks
    private BalanceServiceImpl balanceService;

    @Test
    public void BalanceService_CalculationAdd_AmountAdded() {
        UUID userId = UUID.randomUUID();
        UUID accountId = UUID.randomUUID();
        UUID categoryId = UUID.randomUUID();
        Timestamp datePurchase = Timestamp.from(Instant.now());

        User user = new User(userId, "username", "password", "email@email.com", Timestamp.from(Instant.now()), "USER");
        Account account = new Account(accountId, "Test Account", BigDecimal.valueOf(1000), user, Timestamp.from(Instant.now()));
        Category category = new Category(categoryId, "Test Category", true, user);
        Operation operation = new Operation(UUID.randomUUID(), BigDecimal.valueOf(100), datePurchase, category, account, "Test note", Timestamp.from(Instant.now()));
        balanceService.calculateAccountBalance(account, operation, userId);
        verify(accountService, times(1)).updateAccount(account.getId(), account.getTitle(), new BigDecimal(1100), userId);
    }

    @Test
    public void BalanceService_CalculationAdd_AmountSubtracted() {
        UUID userId = UUID.randomUUID();
        UUID accountId = UUID.randomUUID();
        UUID categoryId = UUID.randomUUID();
        Timestamp datePurchase = Timestamp.from(Instant.now());

        User user = new User(userId, "username", "password", "email@email.com", Timestamp.from(Instant.now()), "USER");
        Account account = new Account(accountId, "Test Account", BigDecimal.valueOf(1000), user, Timestamp.from(Instant.now()));
        Category category = new Category(categoryId, "Test Category", false, user);
        Operation operation = new Operation(UUID.randomUUID(), BigDecimal.valueOf(100), datePurchase, category, account, "Test note", Timestamp.from(Instant.now()));
        balanceService.calculateAccountBalance(account, operation, userId);
        verify(accountService, times(1)).updateAccount(account.getId(), account.getTitle(), new BigDecimal(900), userId);
    }
}
