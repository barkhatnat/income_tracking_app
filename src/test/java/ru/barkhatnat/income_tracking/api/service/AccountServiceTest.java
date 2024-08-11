package ru.barkhatnat.income_tracking.api.service;

import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import ru.barkhatnat.income_tracking.DTO.AccountDto;
import ru.barkhatnat.income_tracking.DTO.AccountResponseDto;
import ru.barkhatnat.income_tracking.entity.Account;
import ru.barkhatnat.income_tracking.entity.User;
import ru.barkhatnat.income_tracking.exception.AccountNotFoundException;
import ru.barkhatnat.income_tracking.exception.ForbiddenException;
import ru.barkhatnat.income_tracking.repositories.AccountRepository;
import ru.barkhatnat.income_tracking.service.AccountServiceImpl;
import ru.barkhatnat.income_tracking.service.UserServiceImpl;
import ru.barkhatnat.income_tracking.utils.AccountMapper;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AccountServiceTest {
    @Mock
    private AccountRepository accountRepository;
    @Mock
    private UserServiceImpl userService;
    @Mock
    private AccountMapper accountMapper;
    @InjectMocks
    private AccountServiceImpl accountService;

    @Test
    public void accountService_CreateAccount_ReturnAccount() {
        UUID userId = UUID.randomUUID();
        UUID accountId = UUID.randomUUID();
        AccountDto accountDto = new AccountDto("Test Category", BigDecimal.valueOf(10000));
        User user = new User(userId, "username", "password", "email@email.com", Timestamp.from(Instant.now()), "USER");
        Account account = new Account(accountId, accountDto.title(), accountDto.balance(), user, Timestamp.from(Instant.now()));
        AccountResponseDto expectedAccountResponseDto = new AccountResponseDto(accountId, "Test Category", BigDecimal.valueOf(10000));

        when(userService.findUser(userId)).thenReturn(Optional.of(user));
        when(accountRepository.save(Mockito.any(Account.class))).thenReturn(account);
        when(accountMapper.toAccountResponseDto(account)).thenReturn(expectedAccountResponseDto);

        AccountResponseDto actualAccountResponseDto = accountService.createAccount(accountDto, userId);
        Assertions.assertThat(actualAccountResponseDto).isEqualTo(expectedAccountResponseDto);
    }

    @Test
    public void accountService_FindAllUserAccounts_ReturnAllUserAccounts() {
        UUID userId = UUID.randomUUID();
        User user = new User(userId, "username", "password", "email@email.com", Timestamp.from(Instant.now()), "USER");
        Account userAccount1 = new Account("userAccount1", BigDecimal.valueOf(10000), user, Timestamp.from(Instant.now()));
        Account userAccount2 = new Account("userAccount2", BigDecimal.valueOf(20000), user, Timestamp.from(Instant.now()));

        when(accountRepository.findAccountsByUserId(userId)).thenReturn(List.of(userAccount1, userAccount2));

        List<Account> accounts = accountService.findAllAccountsByUserId(userId);
        Assertions.assertThat(accounts).hasSize(2);
    }

    @Test
    public void accountService_FindAccountById_ReturnAccount() {
        UUID userId = UUID.randomUUID();
        UUID accountId = UUID.randomUUID();
        User user = new User(userId, "username", "password", "email@email.com", Timestamp.from(Instant.now()), "USER");
        Account account = new Account(accountId, "userAccount2", BigDecimal.valueOf(20000), user, Timestamp.from(Instant.now()));

        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));

        Optional<Account> actualAccount = accountService.findAccount(accountId);
        Assertions.assertThat(actualAccount).isPresent();
        Assertions.assertThat(actualAccount.get()).isEqualTo(account);
    }

    @Test
    public void accountService_UpdateAccountById_ReturnUpdatedAccount() {
        UUID userId = UUID.randomUUID();
        UUID accountId = UUID.randomUUID();
        String newTitle = "Updated Account";
        BigDecimal newBalance = BigDecimal.valueOf(20000);
        User user = new User(userId, "username", "password", "email@email.com", Timestamp.from(Instant.now()), "USER");
        Account account = new Account(accountId, "userAccount2", BigDecimal.valueOf(20000), user, Timestamp.from(Instant.now()));

        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));

        accountService.updateAccount(accountId, newTitle, newBalance, userId);
        Optional<Account> updatedAccount = accountService.findAccount(accountId);

        Assertions.assertThat(updatedAccount).isPresent();
        Assertions.assertThat(updatedAccount.get().getTitle()).isEqualTo(newTitle);
        Assertions.assertThat(updatedAccount.get().getBalance()).isEqualTo(newBalance);
    }

    @Test
    public void accountService_UpdateAccountById_ThrowForbiddenException() {
        UUID userId = UUID.randomUUID();
        UUID otherUserId = UUID.randomUUID();
        UUID accountId = UUID.randomUUID();
        String newTitle = "Updated Account";
        BigDecimal newBalance = BigDecimal.valueOf(20000);
        User otherUser = new User(otherUserId, "username", "password", "email@email.com", Timestamp.from(Instant.now()), "USER");
        Account account = new Account(accountId, "userAccount2", BigDecimal.valueOf(20000), otherUser, Timestamp.from(Instant.now()));

        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));
        assertThrows(ForbiddenException.class,
                () -> accountService.updateAccount(accountId, newTitle, newBalance, userId));
    }

    @Test
    public void accountService_UpdateAccountById_ThrowAccountNotFoundException() {
        UUID accountId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        String newTitle = "Updated Account";
        BigDecimal newBalance = BigDecimal.valueOf(20000);

        when(accountRepository.findById(accountId)).thenReturn(Optional.empty());
        assertThrows(AccountNotFoundException.class,
                () -> accountService.updateAccount(accountId, newTitle, newBalance, userId));
    }

    @Test
    public void accountService_DeleteAccountById_DeleteAccount() {
        UUID userId = UUID.randomUUID();
        UUID accountId = UUID.randomUUID();
        User user = new User(userId, "username", "password", "email@email.com", Timestamp.from(Instant.now()), "USER");
        Account account = new Account(accountId, "userAccount2", BigDecimal.valueOf(20000), user, Timestamp.from(Instant.now()));

        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));

        accountService.deleteAccount(accountId, userId);
        when(accountRepository.findById(accountId)).thenReturn(Optional.empty());
        Assertions.assertThat(accountRepository.findById(accountId)).isEmpty();
        Mockito.verify(accountRepository, Mockito.times(1)).deleteById(accountId);
    }

    @Test
    public void accountService_DeleteAccountById_ThrowForbiddenException() {
        UUID userId = UUID.randomUUID();
        UUID accountId = UUID.randomUUID();
        UUID otherUserId = UUID.randomUUID();
        User otherUser = new User(otherUserId, "username", "password", "email@email.com", Timestamp.from(Instant.now()), "USER");
        Account account = new Account(accountId, "userAccount2", BigDecimal.valueOf(20000), otherUser, Timestamp.from(Instant.now()));

        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));

        assertThrows(ForbiddenException.class,
                () -> accountService.deleteAccount(accountId, userId));
    }

    @Test
    public void accountService_DeleteAccountById_ThrowAccountNotFoundException() {
        UUID accountId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        when(accountRepository.findById(accountId)).thenReturn(Optional.empty());

        assertThrows(AccountNotFoundException.class,
                () -> accountService.deleteAccount(accountId, userId));
    }
}
