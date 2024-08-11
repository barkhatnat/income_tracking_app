package ru.barkhatnat.income_tracking.api.repository;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import ru.barkhatnat.income_tracking.entity.Account;
import ru.barkhatnat.income_tracking.entity.Category;
import ru.barkhatnat.income_tracking.entity.Operation;
import ru.barkhatnat.income_tracking.entity.User;
import ru.barkhatnat.income_tracking.repositories.AccountRepository;
import ru.barkhatnat.income_tracking.repositories.CategoryRepository;
import ru.barkhatnat.income_tracking.repositories.OperationRepository;
import ru.barkhatnat.income_tracking.repositories.UserRepository;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@DataJpaTest
@ActiveProfiles("test")
public class OperationRepositoryTest {
    @Autowired
    private OperationRepository operationRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AccountRepository accountRepository;

    @Test
    public void operationRepositoryTest_SaveOne_ReturnSavedOperation() throws ParseException {
        User user = new User("username", "password", "email@email.com", Timestamp.from(Instant.now()), "USER");
        userRepository.save(user);
        Account account = new Account("Title", BigDecimal.valueOf(1000), user, Timestamp.from(Instant.now()));
        accountRepository.save(account);
        Category category = new Category("Test", Boolean.FALSE, user);
        categoryRepository.save(category);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Timestamp operationDate = new Timestamp(dateFormat.parse("2024-03-15").getTime());
        Operation operation = new Operation(BigDecimal.valueOf(100), operationDate, category, account, "Test note", Timestamp.from(Instant.now()));
        Operation savedOperation = operationRepository.save(operation);
        Assertions.assertThat(savedOperation).isNotNull();
        Assertions.assertThat(savedOperation.getAmount()).isEqualTo(BigDecimal.valueOf(100));
        Assertions.assertThat(savedOperation.getDatePurchase()).isEqualTo(operationDate);
    }

    @Test
    public void operationRepositoryTest_FindAll_ReturnAllSaved() throws ParseException {
        User user = new User("username", "password", "email@email.com", Timestamp.from(Instant.now()), "USER");
        userRepository.save(user);
        Account account = new Account("Title", BigDecimal.valueOf(1000), user, Timestamp.from(Instant.now()));
        accountRepository.save(account);
        Category category = new Category("Test", Boolean.FALSE, user);
        categoryRepository.save(category);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Timestamp operationDate1 = new Timestamp(dateFormat.parse("2024-03-10").getTime());
        Timestamp operationDate2 = new Timestamp(dateFormat.parse("2024-03-12").getTime());
        Operation operation1 = new Operation(BigDecimal.valueOf(50), operationDate1, category, account,
                "Test note 1", Timestamp.from(Instant.now()));
        Operation operation2 = new Operation(BigDecimal.valueOf(100), operationDate2, category, account,
                "Test note 2", Timestamp.from(Instant.now()));
        operationRepository.save(operation1);
        operationRepository.save(operation2);
        List<Operation> operations = operationRepository.findAll();
        Assertions.assertThat(operations.size()).isEqualTo(2);
    }

    @Test
    public void operationRepositoryTest_FindById_ReturnExistingOperation() throws ParseException {
        User user = new User("username", "password", "email@email.com", Timestamp.from(Instant.now()), "USER");
        userRepository.save(user);
        Account account = new Account("Title", BigDecimal.valueOf(1000), user, Timestamp.from(Instant.now()));
        accountRepository.save(account);
        Category category = new Category("Test", Boolean.FALSE, user);
        categoryRepository.save(category);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Timestamp operationDate = new Timestamp(dateFormat.parse("2024-03-15").getTime());
        Operation operation = new Operation(BigDecimal.valueOf(100), operationDate, category, account, "Test note",
                Timestamp.from(Instant.now()));
        Operation savedOperation = operationRepository.save(operation);
        Optional<Operation> foundOperation = operationRepository.findById(savedOperation.getId());
        Assertions.assertThat(foundOperation).isPresent();
        Assertions.assertThat(foundOperation.get()).isEqualTo(savedOperation);
    }

    @Test
    public void operationRepositoryTest_FindById_ReturnEmptyWhenNotFound() {
        UUID uuid = UUID.randomUUID();
        Optional<Operation> foundOperation = operationRepository.findById(uuid); // Предположим, что ID 1 не существует
        Assertions.assertThat(foundOperation).isEmpty();
    }

    @Test
    public void operationRepositoryTest_FindByAccountId_ReturnOperationsOfAccount() throws ParseException {
        User user = new User("username", "password", "email@email.com", Timestamp.from(Instant.now()), "USER");
        userRepository.save(user);
        Account account = new Account("Title", BigDecimal.valueOf(1000), user, Timestamp.from(Instant.now()));
        accountRepository.save(account);
        Category category = new Category("Test", Boolean.FALSE, user);
        categoryRepository.save(category);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Timestamp operationDate1 = new Timestamp(dateFormat.parse("2024-03-10").getTime());
        Timestamp operationDate2 = new Timestamp(dateFormat.parse("2024-03-12").getTime());
        Operation operation1 = new Operation(BigDecimal.valueOf(50), operationDate1, category, account,
                "Test note 1", Timestamp.from(Instant.now()));
        Operation operation2 = new Operation(BigDecimal.valueOf(100), operationDate2, category, account,
                "Test note 2", Timestamp.from(Instant.now()));
        operationRepository.save(operation1);
        operationRepository.save(operation2);

        List<Operation> operations = operationRepository.findOperationsByAccountId(account.getId());
        Assertions.assertThat(operations.size()).isEqualTo(2);
    }

    @Test
    public void operationRepositoryTest_Delete_RemoveExistingOperation() throws ParseException {
        User user = new User("username", "password", "email@email.com", Timestamp.from(Instant.now()), "USER");
        userRepository.save(user);
        Account account = new Account("Title", BigDecimal.valueOf(1000), user, Timestamp.from(Instant.now()));
        accountRepository.save(account);
        Category category = new Category("Test", Boolean.FALSE, user);
        categoryRepository.save(category);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Timestamp operationDate = new Timestamp(dateFormat.parse("2024-03-15").getTime());
        Operation operation = new Operation(BigDecimal.valueOf(100), operationDate, category, account, "Test note",
                Timestamp.from(Instant.now()));
        Operation savedOperation = operationRepository.save(operation);
        operationRepository.delete(savedOperation);
        Optional<Operation> foundOperation = operationRepository.findById(savedOperation.getId());
        Assertions.assertThat(foundOperation).isEmpty();
    }
}
