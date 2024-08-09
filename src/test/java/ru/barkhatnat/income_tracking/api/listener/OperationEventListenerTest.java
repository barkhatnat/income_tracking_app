package ru.barkhatnat.income_tracking.api.listener;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import ru.barkhatnat.income_tracking.entity.Account;
import ru.barkhatnat.income_tracking.entity.Category;
import ru.barkhatnat.income_tracking.entity.Operation;
import ru.barkhatnat.income_tracking.entity.User;
import ru.barkhatnat.income_tracking.event.OperationCreatedEvent;
import ru.barkhatnat.income_tracking.event.OperationDeletedEvent;
import ru.barkhatnat.income_tracking.event.OperationUpdatedEvent;
import ru.barkhatnat.income_tracking.listener.OperationEventListener;
import ru.barkhatnat.income_tracking.service.BalanceService;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class OperationEventListenerTest {

    private BalanceService balanceService;
    private OperationEventListener operationEventListener;
    private Account account;
    private Category expenseCategory;
    private Category incomeCategory;

    @BeforeEach
    public void setUp() {
        balanceService = Mockito.mock(BalanceService.class);
        operationEventListener = new OperationEventListener(balanceService);
        User user = new User(UUID.randomUUID(), "username", "password", "email@email.com", Timestamp.from(Instant.now()), "USER");
        account = new Account(UUID.randomUUID(), "Test Account", BigDecimal.valueOf(1000), user, Timestamp.from(Instant.now()));
        expenseCategory = new Category(UUID.randomUUID(), "Test Category", Boolean.FALSE, user);
        incomeCategory = new Category(UUID.randomUUID(), "Test Category", Boolean.TRUE, user);
    }

    private Operation createOperation(BigDecimal amount, Category category, Account account) {
        return new Operation(UUID.randomUUID(), amount, Timestamp.from(Instant.now()),
                category, account, "Test note", Timestamp.from(Instant.now()));
    }

    private void verifyBalanceServiceInteraction(Account account, BigDecimal amount, Boolean categoryType) {
        ArgumentCaptor<Account> accountCaptor = ArgumentCaptor.forClass(Account.class);
        ArgumentCaptor<BigDecimal> amountCaptor = ArgumentCaptor.forClass(BigDecimal.class);
        ArgumentCaptor<Boolean> categoryTypeCaptor = ArgumentCaptor.forClass(Boolean.class);

        verify(balanceService, times(1)).changeAccountBalanceByDifference(
                accountCaptor.capture(), amountCaptor.capture(), categoryTypeCaptor.capture());
        verifyNoMoreInteractions(balanceService);

        assertEquals(account, accountCaptor.getValue());
        assertEquals(amount, amountCaptor.getValue());
        assertEquals(categoryType, categoryTypeCaptor.getValue());
    }

    @Test
    public void testHandleOperationCreated_ExpenseCategory() {
        BigDecimal operationAmount = BigDecimal.valueOf(100);
        Operation operation = createOperation(operationAmount, expenseCategory, account);
        OperationCreatedEvent event = new OperationCreatedEvent(this, operation);

        operationEventListener.handleOperationCreated(event);

        verifyBalanceServiceInteraction(account, BigDecimal.valueOf(100), expenseCategory.getCategoryType());
    }

    @Test
    public void testHandleOperationCreated_IncomeCategory() {
        BigDecimal operationAmount = BigDecimal.valueOf(100);
        Operation operation = createOperation(operationAmount, incomeCategory, account);
        OperationCreatedEvent event = new OperationCreatedEvent(this, operation);

        operationEventListener.handleOperationCreated(event);

        verifyBalanceServiceInteraction(account, BigDecimal.valueOf(100), incomeCategory.getCategoryType());
    }

    @Test
    public void testHandleOperationUpdated_ExpenseCategory() {
        BigDecimal operationAmount = BigDecimal.valueOf(50);
        Operation operation = createOperation(operationAmount, expenseCategory, account);
        OperationUpdatedEvent event = new OperationUpdatedEvent(this, operation, BigDecimal.valueOf(50));

        operationEventListener.handleOperationUpdated(event);
        verifyBalanceServiceInteraction(account, BigDecimal.valueOf(50), expenseCategory.getCategoryType());
    }

    @Test
    public void testHandleOperationUpdated_IncomeCategory() {
        BigDecimal operationAmount = BigDecimal.valueOf(50);
        Operation operation = createOperation(operationAmount, incomeCategory, account);

        OperationUpdatedEvent event = new OperationUpdatedEvent(this, operation, BigDecimal.valueOf(50));

        operationEventListener.handleOperationUpdated(event);
        verifyBalanceServiceInteraction(account, BigDecimal.valueOf(50), incomeCategory.getCategoryType());
    }

    @Test
    public void testHandleOperationDeleted_ExpenseCategory() {
        BigDecimal operationAmount = BigDecimal.valueOf(100);
        Operation operation = createOperation(operationAmount, expenseCategory, account);
        OperationDeletedEvent event = new OperationDeletedEvent(this, operation);

        operationEventListener.handleOperationDeleted(event);
        verifyBalanceServiceInteraction(account, BigDecimal.valueOf(100), incomeCategory.getCategoryType());
    }

    @Test
    public void testHandleOperationDeleted_IncomeCategory() {
        BigDecimal operationAmount = BigDecimal.valueOf(100);
        Operation operation = createOperation(operationAmount, incomeCategory, account);
        OperationDeletedEvent event = new OperationDeletedEvent(this, operation);

        operationEventListener.handleOperationDeleted(event);
        verifyBalanceServiceInteraction(account, BigDecimal.valueOf(100), expenseCategory.getCategoryType());
    }
}
