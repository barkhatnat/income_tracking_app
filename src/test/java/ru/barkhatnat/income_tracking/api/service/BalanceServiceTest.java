package ru.barkhatnat.income_tracking.api.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.barkhatnat.income_tracking.entity.Account;
import ru.barkhatnat.income_tracking.entity.User;
import ru.barkhatnat.income_tracking.repositories.AccountRepository;
import ru.barkhatnat.income_tracking.service.BalanceServiceImpl;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.UUID;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BalanceServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private BalanceServiceImpl balanceService;

    private Account account;

    @BeforeEach
    public void setUp() {
        User user = new User();
        user.setId(UUID.randomUUID());
        account = new Account(UUID.randomUUID(), "Test Account", BigDecimal.valueOf(1000), user, new Timestamp(System.currentTimeMillis()));
    }

    @Test
    public void testChangeAccountBalanceByDifference_addition() {
        BigDecimal difference = BigDecimal.valueOf(100);
        Boolean categoryType = true;
        System.out.println(account);

        balanceService.changeAccountBalanceByDifference(account, difference, categoryType);

        BigDecimal expectedBalance = BigDecimal.valueOf(1100);
        verify(accountRepository, times(1)).updateAccountBalance(eq(account.getId()), eq(expectedBalance));
    }

    @Test
    public void testChangeAccountBalanceByDifference_subtraction() {
        BigDecimal difference = BigDecimal.valueOf(100);
        Boolean categoryType = false;

        balanceService.changeAccountBalanceByDifference(account, difference, categoryType);

        BigDecimal expectedBalance = BigDecimal.valueOf(900);
        verify(accountRepository, times(1)).updateAccountBalance(eq(account.getId()), eq(expectedBalance));
    }
}
