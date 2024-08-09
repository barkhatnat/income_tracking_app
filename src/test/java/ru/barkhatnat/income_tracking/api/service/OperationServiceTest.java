package ru.barkhatnat.income_tracking.api.service;

import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.context.ApplicationEventPublisher;
import ru.barkhatnat.income_tracking.DTO.OperationDto;
import ru.barkhatnat.income_tracking.DTO.OperationResponseDto;
import ru.barkhatnat.income_tracking.entity.Account;
import ru.barkhatnat.income_tracking.entity.Category;
import ru.barkhatnat.income_tracking.entity.Operation;
import ru.barkhatnat.income_tracking.entity.User;
import ru.barkhatnat.income_tracking.exception.AccountNotFoundException;
import ru.barkhatnat.income_tracking.exception.CategoryNotFoundException;
import ru.barkhatnat.income_tracking.exception.ForbiddenException;
import ru.barkhatnat.income_tracking.exception.OperationNotFoundException;
import ru.barkhatnat.income_tracking.repositories.OperationRepository;
import ru.barkhatnat.income_tracking.service.AccountService;
import ru.barkhatnat.income_tracking.service.CategoryService;
import ru.barkhatnat.income_tracking.service.OperationServiceImpl;
import ru.barkhatnat.income_tracking.utils.OperationMapper;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class OperationServiceTest {
    @Mock
    private OperationRepository operationRepository;
    @Mock
    private OperationMapper operationMapper;
    @Mock
    private CategoryService categoryService;
    @Mock
    private AccountService accountService;
    @Mock
    private ApplicationEventPublisher eventPublisher;
    @InjectMocks
    private OperationServiceImpl operationService;


    @Test
    public void OperationService_CreateOperation_ReturnOperationResponseDto() {
        UUID userId = UUID.randomUUID();
        UUID accountId = UUID.randomUUID();
        UUID categoryId = UUID.randomUUID();
        BigDecimal amount = BigDecimal.valueOf(100.00);
        Timestamp datePurchase = Timestamp.from(Instant.now());
        String note = "Test note";

        User user = new User(userId, "username", "password", "email@email.com", Timestamp.from(Instant.now()), "USER");
        Account account = new Account(accountId, "Test Account", BigDecimal.valueOf(1000), user, Timestamp.from(Instant.now()));
        Category category = new Category(categoryId, "Test Category", false, user);
        OperationDto operationDto = new OperationDto(amount, datePurchase, categoryId, note);
        Operation operation = new Operation(UUID.randomUUID(), amount, datePurchase, category, account, note, Timestamp.from(Instant.now()));
        OperationResponseDto expectedOperationResponseDto = new OperationResponseDto(operation.getId(), amount, datePurchase, categoryId, note);

        when(categoryService.findCategory(categoryId)).thenReturn(Optional.of(operation.getCategory()));
        when(accountService.findAccount(accountId)).thenReturn(Optional.of(operation.getAccount()));
        when(operationRepository.save(Mockito.any(Operation.class))).thenReturn(operation);
        when(operationMapper.toOperationResponseDto(operation)).thenReturn(expectedOperationResponseDto);
        OperationResponseDto actualOperationResponseDto = operationService.createOperation(operationDto, accountId, userId);
        Assertions.assertThat(actualOperationResponseDto).isEqualTo(expectedOperationResponseDto);
    }

    @Test
    public void OperationService_CreateOperation_ThrowAccountNotFoundException() {
        UUID userId = UUID.randomUUID();
        UUID accountId = UUID.randomUUID();
        UUID categoryId = UUID.randomUUID();
        BigDecimal amount = BigDecimal.valueOf(100.00);
        Timestamp datePurchase = Timestamp.from(Instant.now());
        String note = "Test note";

        User user = new User(userId, "username", "password", "email@email.com", Timestamp.from(Instant.now()), "USER");
        Account account = new Account(accountId, "Test Account", BigDecimal.valueOf(1000), user, Timestamp.from(Instant.now()));
        Category category = new Category(categoryId, "Test Category", false, user);
        OperationDto operationDto = new OperationDto(amount, datePurchase, categoryId, note);
        Operation operation = new Operation(UUID.randomUUID(), amount, datePurchase, category, account, note, Timestamp.from(Instant.now()));

        when(categoryService.findCategory(categoryId)).thenReturn(Optional.of(operation.getCategory()));
        when(accountService.findAccount(accountId)).thenReturn(Optional.empty());

        assertThrows(AccountNotFoundException.class,
                () -> operationService.createOperation(operationDto, accountId, userId));
    }

    @Test
    public void OperationService_CreateOperation_ThrowCategoryNotFoundException() {
        UUID userId = UUID.randomUUID();
        UUID accountId = UUID.randomUUID();
        UUID categoryId = UUID.randomUUID();
        BigDecimal amount = BigDecimal.valueOf(100.00);
        Timestamp datePurchase = Timestamp.from(Instant.now());
        String note = "Test note";

        OperationDto operationDto = new OperationDto(amount, datePurchase, categoryId, note);

        when(categoryService.findCategory(categoryId)).thenReturn(Optional.empty());

        assertThrows(CategoryNotFoundException.class,
                () -> operationService.createOperation(operationDto, accountId, userId));
    }

    @Test
    public void OperationService_FindAllOperationsByAccountId_ReturnListOfOperations() {
        UUID userId = UUID.randomUUID();
        UUID accountId = UUID.randomUUID();
        UUID categoryId = UUID.randomUUID();

        User user = new User(userId, "username", "password", "email@email.com", Timestamp.from(Instant.now()), "USER");
        Account account = new Account(accountId, "Test Account", BigDecimal.valueOf(1000), user, Timestamp.from(Instant.now()));
        Category category = new Category(categoryId, "Test Category", false, user);
        Operation operation1 = new Operation(UUID.randomUUID(), BigDecimal.valueOf(100), Timestamp.from(Instant.now()), category, account, "Note", Timestamp.from(Instant.now()));
        Operation operation2 = new Operation(UUID.randomUUID(), BigDecimal.valueOf(200), Timestamp.from(Instant.now()), category, account, "Note", Timestamp.from(Instant.now()));

        when(operationRepository.findOperationsByAccountId(accountId)).thenReturn(List.of(operation1, operation2));
        when(accountService.findAccount(accountId)).thenReturn(Optional.of(account));
        List<Operation> actualOperations = operationService.findAllOperationsByAccountId(accountId, userId);
        Assertions.assertThat(actualOperations).containsExactlyInAnyOrder(operation1, operation2);
        Assertions.assertThat(actualOperations.size()).isEqualTo(2);
    }

    @Test
    public void OperationService_FindAllOperationsByAccountId_ThrowForbiddenExceptionByUser() {
        UUID userId = UUID.randomUUID();
        UUID otherUserId = UUID.randomUUID();
        UUID accountId = UUID.randomUUID();

        User user = new User(userId, "username", "password", "email@email.com", Timestamp.from(Instant.now()), "USER");
        Account account = new Account(accountId, "Test Account", BigDecimal.valueOf(1000), user, Timestamp.from(Instant.now()));

        when(accountService.findAccount(accountId)).thenReturn(Optional.of(account));

        assertThrows(ForbiddenException.class,
                () -> operationService.findAllOperationsByAccountId(accountId, otherUserId));
    }

    @Test
    public void OperationService_FindAllOperationsByAccountId_ThrowForbiddenExceptionByAccount() {
        UUID userId = UUID.randomUUID();
        UUID otherAccountId = UUID.randomUUID();

        assertThrows(ForbiddenException.class,
                () -> operationService.findAllOperationsByAccountId(otherAccountId, userId));
    }

    @Test
    public void OperationService_FindOperation_ReturnOptionalOperation() {
        UUID userId = UUID.randomUUID();
        UUID accountId = UUID.randomUUID();
        UUID categoryId = UUID.randomUUID();
        UUID operationId = UUID.randomUUID();

        User user = new User(userId, "username", "password", "email@email.com", Timestamp.from(Instant.now()), "USER");
        Account account = new Account(accountId, "Test Account", BigDecimal.valueOf(1000), user, Timestamp.from(Instant.now()));
        Category category = new Category(categoryId, "Test Category", false, user);
        Operation operation = new Operation(operationId, BigDecimal.valueOf(100), Timestamp.from(Instant.now()), category, account, "Note", Timestamp.from(Instant.now()));

        when(operationRepository.findById(operationId)).thenReturn(Optional.of(operation));

        Optional<Operation> actualOperation = operationService.findOperation(operationId);
        Assertions.assertThat(actualOperation).isPresent();
        Assertions.assertThat(actualOperation.get()).isEqualTo(operation);
    }

    @Test
    public void OperationService_UpdateOperation_OperationUpdated() {
        UUID userId = UUID.randomUUID();
        UUID accountId = UUID.randomUUID();
        UUID categoryId = UUID.randomUUID();
        UUID operationId = UUID.randomUUID();
        BigDecimal newAmount = BigDecimal.valueOf(1000.00);

        User user = new User(userId, "username", "password", "email@email.com", Timestamp.from(Instant.now()), "USER");
        Account account = new Account(accountId, "Test Account", BigDecimal.valueOf(1000), user, Timestamp.from(Instant.now()));
        Category category = new Category(categoryId, "Test Category", false, user);
        Operation operation = new Operation(operationId, BigDecimal.valueOf(100), Timestamp.from(Instant.now()), category, account, "Note", Timestamp.from(Instant.now()));

        when(operationRepository.findById(operationId)).thenReturn(Optional.of(operation));
        when(accountService.findAccount(accountId)).thenReturn(Optional.of(account));
        when(categoryService.findCategory(categoryId)).thenReturn(Optional.of(category));

        operationService.updateOperation(operationId, newAmount, Timestamp.from(Instant.now()), categoryId, "Note", accountId, userId);
        Optional<Operation> updatedOperation = operationRepository.findById(operationId);

        Assertions.assertThat(updatedOperation).isPresent();
        Assertions.assertThat(updatedOperation.get().getAmount()).isEqualTo(newAmount);
    }

    @Test
    public void OperationService_UpdateOperation_ThrowCategoryNotFoundException() {
        UUID userId = UUID.randomUUID();
        UUID accountId = UUID.randomUUID();
        UUID categoryId = UUID.randomUUID();
        UUID operationId = UUID.randomUUID();
        BigDecimal newAmount = BigDecimal.valueOf(1000.00);

        User user = new User(userId, "username", "password", "email@email.com", Timestamp.from(Instant.now()), "USER");
        Account account = new Account(accountId, "Test Account", BigDecimal.valueOf(1000), user, Timestamp.from(Instant.now()));
        Category category = new Category(categoryId, "Test Category", false, user);
        Operation operation = new Operation(operationId, BigDecimal.valueOf(100), Timestamp.from(Instant.now()), category, account, "Note", Timestamp.from(Instant.now()));

        when(operationRepository.findById(operationId)).thenReturn(Optional.of(operation));
        when(accountService.findAccount(accountId)).thenReturn(Optional.of(account));
        when(categoryService.findCategory(categoryId)).thenReturn(Optional.empty());

        assertThrows(CategoryNotFoundException.class,
                () -> operationService.updateOperation(operationId, newAmount, Timestamp.from(Instant.now()), categoryId, "Note", accountId, userId));
    }

    @Test
    public void OperationService_UpdateOperation_ThrowOperationNotFoundException() {
        UUID userId = UUID.randomUUID();
        UUID accountId = UUID.randomUUID();
        UUID categoryId = UUID.randomUUID();
        UUID operationId = UUID.randomUUID();
        BigDecimal newAmount = BigDecimal.valueOf(1000.00);

        when(operationRepository.findById(operationId)).thenReturn(Optional.empty());

        assertThrows(OperationNotFoundException.class,
                () -> operationService.updateOperation(operationId, newAmount, Timestamp.from(Instant.now()), categoryId, "Note", accountId, userId));
    }

    @Test
    public void OperationService_DeleteOperation_SuccessDeleteOperation() {
        UUID userId = UUID.randomUUID();
        UUID accountId = UUID.randomUUID();
        UUID categoryId = UUID.randomUUID();
        UUID operationId = UUID.randomUUID();

        User user = new User(userId, "username", "password", "email@email.com", Timestamp.from(Instant.now()), "USER");
        Account account = new Account(accountId, "Test Account", BigDecimal.valueOf(1000), user, Timestamp.from(Instant.now()));
        Category category = new Category(categoryId, "Test Category", false, user);
        Operation operation = new Operation(operationId, BigDecimal.valueOf(100), Timestamp.from(Instant.now()), category, account, "Note", Timestamp.from(Instant.now()));
        when(operationRepository.findById(operationId)).thenReturn(Optional.of(operation));
        when(accountService.findAccount(accountId)).thenReturn(Optional.of(account));

        operationService.deleteOperation(operationId, accountId, userId);
        when(operationRepository.findById(operationId)).thenReturn(Optional.empty());
        Optional<Operation> deletedOperation = operationRepository.findById(operationId);
        Assertions.assertThat(deletedOperation).isEmpty();
    }

    @Test
    public void OperationService_DeleteOperation_ThrowOperationNotFoundException() {
        UUID userId = UUID.randomUUID();
        UUID accountId = UUID.randomUUID();
        UUID otherOperationId = UUID.randomUUID();

        assertThrows(OperationNotFoundException.class,
                () -> operationService.deleteOperation(otherOperationId, accountId, userId));
    }

}
