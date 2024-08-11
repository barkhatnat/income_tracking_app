package ru.barkhatnat.income_tracking.api.repository;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import ru.barkhatnat.income_tracking.entity.Account;
import ru.barkhatnat.income_tracking.entity.User;
import ru.barkhatnat.income_tracking.repositories.AccountRepository;
import ru.barkhatnat.income_tracking.repositories.UserRepository;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@DataJpaTest
@ActiveProfiles("test")
public class AccountRepositoryTest {
    @Autowired
    AccountRepository accountRepository;
    @Autowired
    private UserRepository userRepository;

    @Test
    public void accountRepositoryTest_SaveOne_ReturnSavedAccount() {
        User user = new User("username", "password", "email@email.com", Timestamp.from(Instant.now()), "USER");
        userRepository.save(user);
        Account account = new Account("Title", BigDecimal.valueOf(1000), user, Timestamp.from(Instant.now()));
        Account savedAccount = accountRepository.save(account);
        Assertions.assertThat(savedAccount).isNotNull();
        Assertions.assertThat(savedAccount.getTitle()).isEqualTo("Title");
        Assertions.assertThat(savedAccount.getBalance()).isEqualTo(BigDecimal.valueOf(1000));
    }

    @Test
    public void accountRepositoryTest_FindAll_ReturnAllSaved() {
        User user = new User("username", "password", "email@email.com", Timestamp.from(Instant.now()), "USER");
        userRepository.save(user);
        Account account1 = new Account("Title1", BigDecimal.valueOf(1000), user, Timestamp.from(Instant.now()));
        Account account2 = new Account("Title2", BigDecimal.valueOf(2000), user, Timestamp.from(Instant.now()));
        accountRepository.save(account1);
        accountRepository.save(account2);
        List<Account> accounts = accountRepository.findAll();
        Assertions.assertThat(accounts.size()).isEqualTo(2);
    }

    @Test
    public void accountRepositoryTest_FindById_ReturnExistingAccount() {
        User user = new User("username", "password", "email@email.com", Timestamp.from(Instant.now()), "USER");
        userRepository.save(user);
        Account account = new Account("Title", BigDecimal.valueOf(1000), user, Timestamp.from(Instant.now()));
        Account savedAccount = accountRepository.save(account);
        Optional<Account> foundAccount = accountRepository.findById(savedAccount.getId());
        Assertions.assertThat(foundAccount).isPresent();
        Assertions.assertThat(foundAccount.get()).isEqualTo(savedAccount);
    }

    @Test
    public void accountRepositoryTest_FindById_ReturnEmptyWhenNotFound() {
        UUID uuid = UUID.randomUUID();
        Optional<Account> foundAccount = accountRepository.findById(uuid); // Предположим, что ID 1 не существует
        Assertions.assertThat(foundAccount).isEmpty();
    }

    @Test
    public void accountRepositoryTest_FindByUserId_ReturnAccountsOfUser() {
        User user = new User("username", "password", "email@email.com", Timestamp.from(Instant.now()), "USER");
        userRepository.save(user);
        Account account1 = new Account("Title1", BigDecimal.valueOf(1000), user, Timestamp.from(Instant.now()));
        Account account2 = new Account("Title2", BigDecimal.valueOf(2000), user, Timestamp.from(Instant.now()));
        accountRepository.save(account1);
        accountRepository.save(account2);
        List<Account> accounts = accountRepository.findAccountsByUserId(user.getId());
        Assertions.assertThat(accounts.size()).isEqualTo(2);
    }

    @Test
    public void accountRepositoryTest_Delete_RemoveExistingAccount() {
        User user = new User("username", "password", "email@email.com", Timestamp.from(Instant.now()), "USER");
        userRepository.save(user);
        Account account = new Account("Title", BigDecimal.valueOf(1000), user, Timestamp.from(Instant.now()));
        Account savedAccount = accountRepository.save(account);
        accountRepository.delete(savedAccount);
        Optional<Account> foundAccount = accountRepository.findById(savedAccount.getId());
        Assertions.assertThat(foundAccount).isEmpty();
    }
}
