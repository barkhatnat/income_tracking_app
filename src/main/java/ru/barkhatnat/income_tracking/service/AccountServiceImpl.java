package ru.barkhatnat.income_tracking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.barkhatnat.income_tracking.DTO.AccountDto;
import ru.barkhatnat.income_tracking.DTO.AccountResponseDto;
import ru.barkhatnat.income_tracking.entity.Account;
import ru.barkhatnat.income_tracking.entity.Operation;
import ru.barkhatnat.income_tracking.entity.User;
import ru.barkhatnat.income_tracking.repositories.AccountRepository;
import ru.barkhatnat.income_tracking.utils.AccountMapper;
import ru.barkhatnat.income_tracking.utils.SecurityUtil;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {
    private final AccountRepository accountRepository;
    private final UserServiceImpl userService;
    private final AccountMapper accountMapper;

    @Override
    @Transactional
    public Iterable<Account> findAllAccounts() {
        UUID id = SecurityUtil.getCurrentUserDetails().getUserId();
        return accountRepository.findAccountsByUserId(id);
    }

    @Override
    public Iterable<Operation> findAllAccountOperations(Account account) {
        return account.getOperations();
    }

    @Override
    @Transactional
    public AccountResponseDto createAccount(AccountDto accountDto) {
        UUID id = SecurityUtil.getCurrentUserDetails().getUserId();
        Optional<User> user = userService.findUser(id);
        if (user.isEmpty()) {
            throw new NoSuchElementException(); //TODO сделать кастомный эксепшн
        }
        Account account = accountRepository.save(new Account(accountDto.title(), accountDto.balance(), user.get(), getCreationDate()));
        return accountMapper.toAccountResponseDto(account);
    }

    @Override
    @Transactional
    public Optional<Account> findAccount(UUID id) {
        return accountRepository.findById(id);
    }

    @Override
    @Transactional
    public void updateAccount(UUID id, String title, BigDecimal balance) {

        accountRepository.findById(id).ifPresentOrElse(account -> {
                    if (account.getUser() != null && account.getUser().getId().equals(SecurityUtil.getCurrentUserDetails().getUserId())) {
                        account.setTitle(title);
                        account.setBalance(balance);
                    } else {
                        throw new IllegalArgumentException("You do not have permission to update this account.");
                    }
                }, () -> {
                    throw new NoSuchElementException();
                }
        );
    }

    @Override
    @Transactional
    public void deleteAccount(UUID id) {
        accountRepository.findById(id).ifPresentOrElse(account -> {
                    if (account.getUser() != null && account.getUser().getId().equals(SecurityUtil.getCurrentUserDetails().getUserId())) {
                        accountRepository.deleteById(id);
                    } else {
                        throw new IllegalArgumentException("You do not have permission to delete this account.");
                    }
                }, () -> {
                    throw new NoSuchElementException();
                }
        );
    }

    private Timestamp getCreationDate() {
        return Timestamp.from(Instant.now());
    }
}
