package ru.barkhatnat.income_tracking.api.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import ru.barkhatnat.income_tracking.DTO.OperationDto;
import ru.barkhatnat.income_tracking.DTO.OperationResponseDto;
import ru.barkhatnat.income_tracking.entity.Account;
import ru.barkhatnat.income_tracking.entity.Category;
import ru.barkhatnat.income_tracking.entity.Operation;
import ru.barkhatnat.income_tracking.entity.User;
import ru.barkhatnat.income_tracking.exception.CategoryTypeException;
import ru.barkhatnat.income_tracking.repositories.AccountRepository;
import ru.barkhatnat.income_tracking.repositories.CategoryRepository;
import ru.barkhatnat.income_tracking.repositories.OperationRepository;
import ru.barkhatnat.income_tracking.repositories.UserRepository;
import ru.barkhatnat.income_tracking.service.OperationService;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class OperationServiceIntegrationTest {

    @Autowired
    private OperationService operationService;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private OperationRepository operationRepository;

    private Account account;
    private Category expenseCategory;
    private Category newExpenseCategory;
    private Category incomeCategory;
    private Category newIncomeCategory;
    private Operation expenseOperation;
    private Operation incomeOperation;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    public void setUp() {
        User user = new User("username", "password", "email@email.com", Timestamp.from(Instant.now()), "USER");
        userRepository.saveAndFlush(user);
        account = new Account("Test Account", BigDecimal.valueOf(1000), user, Timestamp.from(Instant.now()));
        accountRepository.saveAndFlush(account);
        expenseCategory = new Category("Expense Category", Boolean.FALSE, user);
        categoryRepository.saveAndFlush(expenseCategory);
        newExpenseCategory = new Category("New Expense Category", Boolean.FALSE, user);
        categoryRepository.saveAndFlush(newExpenseCategory);
        incomeCategory = new Category("Income Category", Boolean.TRUE, user);
        categoryRepository.saveAndFlush(incomeCategory);
        newIncomeCategory = new Category("New Income Category", Boolean.TRUE, user);
        categoryRepository.saveAndFlush(newIncomeCategory);
        expenseOperation = new Operation(BigDecimal.valueOf(500), Timestamp.from(Instant.now()),
                expenseCategory, account, "Test note", Timestamp.from(Instant.now()));
        operationRepository.saveAndFlush(expenseOperation);
        incomeOperation = new Operation(BigDecimal.valueOf(500), Timestamp.from(Instant.now()),
                incomeCategory, account, "Test note", Timestamp.from(Instant.now()));
        operationRepository.saveAndFlush(incomeOperation);
    }

    @AfterEach
    public void cleanUp() {
        operationRepository.deleteAll();
        categoryRepository.deleteAllInBatch();
        accountRepository.deleteAllInBatch();
        userRepository.deleteAllInBatch();
    }

    @ParameterizedTest
    @CsvSource({
            "100, 900, EXPENSE",
            "100, 1100, INCOME"
    })
    public void testCreateOperation_BalanceUpdated(BigDecimal operationAmount, BigDecimal expectedBalance, String categoryType) {
        UUID userId = account.getUser().getId();
        UUID currentAccountId = account.getId();
        Category category = categoryType.equals("EXPENSE") ? expenseCategory : incomeCategory;

        OperationDto operationDto = new OperationDto(operationAmount, Timestamp.from(Instant.now()), category.getId(), "Test note");
        OperationResponseDto response = operationService.createOperation(operationDto, currentAccountId, userId);

        assertThat(operationRepository.findById(response.id())).isPresent();
        assertThat(accountRepository.findById(currentAccountId))
                .isPresent()
                .get()
                .hasFieldOrPropertyWithValue("balance", expectedBalance);
    }

    @ParameterizedTest
    @CsvSource({
            "1500, EXPENSE",
            "500, INCOME"
    })
    public void testDeleteOperation_BalanceUpdated(BigDecimal expectedBalance, String categoryType) {
        UUID userId = account.getUser().getId();
        UUID currentAccountId = account.getId();
        Operation operation = categoryType.equals("EXPENSE") ? expenseOperation : incomeOperation;
        UUID operationId = operation.getId();
        operationService.deleteOperation(operationId, currentAccountId, userId);
        Optional<Operation> deletedOperation = operationRepository.findById(operationId);
        assertThat(deletedOperation).isEmpty();
        assertThat(accountRepository.findById(currentAccountId))
                .isPresent()
                .get()
                .hasFieldOrPropertyWithValue("balance", expectedBalance);
    }

    @Test
    public void testCreateAndDeleteOperation_ExpenseCategory_BalanceTheSame() {
        BigDecimal operationAmount = BigDecimal.valueOf(100);
        OperationDto operationDto = new OperationDto(operationAmount, Timestamp.from(Instant.now()),
                expenseCategory.getId(), "Test note");

        UUID userId = account.getUser().getId();
        UUID currentAccountId = account.getId();

        OperationResponseDto operationResponseDto = operationService.createOperation(operationDto, currentAccountId, userId);
        Optional<Operation> operation = operationRepository.findById(operationResponseDto.id());
        assertThat(operation)
                .isPresent().get()
                .hasFieldOrPropertyWithValue("amount", BigDecimal.valueOf(100));
        operationService.deleteOperation(operation.get().getId(), currentAccountId, userId);
        Optional<Operation> deletedOperation = operationRepository.findById(operationResponseDto.id());
        assertThat(deletedOperation).isEmpty();
        assertThat(accountRepository.findById(currentAccountId))
                .isPresent()
                .get()
                .hasFieldOrPropertyWithValue("balance", BigDecimal.valueOf(1000));
    }

    @ParameterizedTest
    @CsvSource({
            "1000, 500, 500, EXPENSE", // Test for increase amount in EXPENSE category (Balance Reduced)
            "1000, 500, 1500, INCOME", // Test for increase amount in INCOME category (Balance Increased)
            "200, 500, 1300, EXPENSE", // Test for reduce amount in EXPENSE category (Balance Increased)
            "200, 500, 700, INCOME"   // Test for reduce amount in INCOME category (Balance Reduced)
    })
    public void testUpdateOperation(BigDecimal newAmount, BigDecimal oldAmount, BigDecimal expectedBalance, String category) {
        // Setup
        UUID userId = account.getUser().getId();
        UUID currentAccountId = account.getId();
        UUID operationId = category.equals("EXPENSE") ? expenseOperation.getId() : incomeOperation.getId();

        Optional<Operation> operation = operationService.findOperation(operationId);
        assertThat(operation)
                .isPresent().get()
                .hasFieldOrPropertyWithValue("amount", oldAmount);

        operationService.updateOperation(operationId, newAmount, operation.get().getDatePurchase(), operation.get().getCategory().getId(),
                operation.get().getNote(), operation.get().getAccount().getId(), userId);

        Optional<Operation> updatedOperation = operationService.findOperation(operationId);
        assertThat(updatedOperation)
                .get()
                .hasFieldOrPropertyWithValue("amount", newAmount);

        assertThat(accountRepository.findById(currentAccountId))
                .isPresent()
                .get()
                .hasFieldOrPropertyWithValue("balance", expectedBalance);
    }

    @Test
    public void testUpdateOperation_ChangeCategory_FromExpenseCategoryToIncomeCategory_ThrowException() {
        Category newCategory = incomeCategory;
        UUID userId = account.getUser().getId();
        UUID operationId = expenseOperation.getId();
        Optional<Operation> operation = operationService.findOperation(operationId);
        assertThat(operation).isPresent().get().hasNoNullFieldsOrProperties();
        assertThatThrownBy(() -> operationService.updateOperation(operationId, operation.get().getAmount(), operation.get().getDatePurchase(), newCategory.getId(),
                operation.get().getNote(), operation.get().getAccount().getId(), userId)).isInstanceOf(CategoryTypeException.class)
                .hasMessageContaining("It is not possible to change the operation category to a different type of category");
    }

    @Test
    public void testUpdateOperation_ChangeCategory_FromIncomeCategoryToIncomeCategory_Success() {
        UUID userId = account.getUser().getId();
        UUID operationId = incomeOperation.getId();
        Optional<Operation> operation = operationService.findOperation(operationId);
        assertThat(operation).isPresent().get().hasNoNullFieldsOrProperties();
        assertThat(operation.get().getCategory().getTitle()).isEqualTo(incomeCategory.getTitle());
        operationService.updateOperation(operationId, operation.get().getAmount(), operation.get().getDatePurchase(), newIncomeCategory.getId(),
                operation.get().getNote(), operation.get().getAccount().getId(), userId);
        Optional<Operation> updatedOperation = operationService.findOperation(operationId);
        assertThat(updatedOperation).isPresent().get().hasNoNullFieldsOrProperties();
        assertThat(updatedOperation.get().getCategory().getTitle()).isEqualTo(newIncomeCategory.getTitle());
    }

    @Test
    public void testUpdateOperation_ChangeCategory_FromIncomeCategoryToExpenseCategory_ThrowException() {
        Category newCategory = expenseCategory;
        UUID userId = account.getUser().getId();
        UUID operationId = incomeOperation.getId();
        Optional<Operation> operation = operationService.findOperation(operationId);
        assertThat(operation).isPresent().get().hasNoNullFieldsOrProperties();
        assertThatThrownBy(() -> operationService.updateOperation(operationId, operation.get().getAmount(), operation.get().getDatePurchase(), newCategory.getId(),
                operation.get().getNote(), operation.get().getAccount().getId(), userId)).isInstanceOf(CategoryTypeException.class)
                .hasMessageContaining("It is not possible to change the operation category to a different type of category");
    }

    @Test
    public void testUpdateOperation_ChangeCategory_FromExpenseCategoryToExpenseCategory_Success() {
        UUID userId = account.getUser().getId();
        UUID operationId = expenseOperation.getId();
        Optional<Operation> operation = operationService.findOperation(operationId);
        assertThat(operation).isPresent().get().hasNoNullFieldsOrProperties();
        assertThat(operation.get().getCategory().getTitle()).isEqualTo(expenseCategory.getTitle());
        operationService.updateOperation(operationId, operation.get().getAmount(), operation.get().getDatePurchase(), newExpenseCategory.getId(),
                operation.get().getNote(), operation.get().getAccount().getId(), userId);
        Optional<Operation> updatedOperation = operationService.findOperation(operationId);
        assertThat(updatedOperation).isPresent().get().hasNoNullFieldsOrProperties();
        assertThat(updatedOperation.get().getCategory().getTitle()).isEqualTo(newExpenseCategory.getTitle());
    }

    @ParameterizedTest
    @CsvSource({
            "EXPENSE, INCOME, false",  // From EXPENSE to INCOME - Should throw exception
            "INCOME, INCOME, true",    // From INCOME to INCOME - Should succeed
            "INCOME, EXPENSE, false",  // From INCOME to EXPENSE - Should throw exception
            "EXPENSE, EXPENSE, true"   // From EXPENSE to EXPENSE - Should succeed
    })
    public void testUpdateOperation_ChangeCategory(String currentCategoryType, String newCategoryType, boolean shouldSucceed) {
        UUID userId = account.getUser().getId();
        UUID operationId = currentCategoryType.equals("EXPENSE") ? expenseOperation.getId() : incomeOperation.getId();
        Category currentCategory = currentCategoryType.equals("EXPENSE") ? expenseCategory : incomeCategory;
        Category newCategory = newCategoryType.equals("EXPENSE") ? newExpenseCategory : newIncomeCategory;

        Optional<Operation> operation = operationService.findOperation(operationId);
        assertThat(operation).isPresent().get().hasNoNullFieldsOrProperties();

        if (shouldSucceed) {
            assertThat(operation.get().getCategory().getTitle()).isEqualTo(currentCategory.getTitle());
            operationService.updateOperation(operationId, operation.get().getAmount(), operation.get().getDatePurchase(), newCategory.getId(),
                    operation.get().getNote(), operation.get().getAccount().getId(), userId);
            Optional<Operation> updatedOperation = operationService.findOperation(operationId);
            assertThat(updatedOperation).isPresent().get().hasNoNullFieldsOrProperties();
            assertThat(updatedOperation.get().getCategory().getTitle()).isEqualTo(newCategory.getTitle());
        } else {
            assertThatThrownBy(() -> operationService.updateOperation(operationId, operation.get().getAmount(), operation.get().getDatePurchase(), newCategory.getId(),
                    operation.get().getNote(), operation.get().getAccount().getId(), userId)).isInstanceOf(CategoryTypeException.class)
                    .hasMessageContaining("It is not possible to change the operation category to a different type of category");
        }
    }
}
