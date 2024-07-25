package ru.barkhatnat.income_tracking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.barkhatnat.income_tracking.DTO.AccountDto;
import ru.barkhatnat.income_tracking.DTO.AccountResponseDto;
import ru.barkhatnat.income_tracking.entity.Account;
import ru.barkhatnat.income_tracking.entity.User;
import ru.barkhatnat.income_tracking.exception.AccountNotFoundException;
import ru.barkhatnat.income_tracking.exception.ForbiddenException;
import ru.barkhatnat.income_tracking.exception.UserNotFoundException;
import ru.barkhatnat.income_tracking.repositories.AccountRepository;
import ru.barkhatnat.income_tracking.utils.AccountMapper;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
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
    public List<Account> findAllAccountsByUserId(UUID userId) {
        return accountRepository.findAccountsByUserId(userId);
    }

    @Override
    @Transactional
    public AccountResponseDto createAccount(AccountDto accountDto, UUID userId) {
        User user = userService.findUser(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
        Account account = accountRepository.save(
                new Account(accountDto.title(), accountDto.balance(), user, getCreationDate())
        );
        return accountMapper.toAccountResponseDto(account);
    }

    @Override
    @Transactional
    public Optional<Account> findAccount(UUID id) {
        return accountRepository.findById(id);
    }

    @Override
    @Transactional
    public void updateAccount(UUID id, String title, BigDecimal balance, UUID userId) {
        accountRepository.findById(id).ifPresentOrElse(account -> {
                    checkAccountOwnership(id, userId);
                    account.setTitle(title);
                    account.setBalance(balance);
                }, () -> {
                    throw new AccountNotFoundException(id);
                }
        );
    }

    @Override
    @Transactional
    public void deleteAccount(UUID id, UUID userId) {
        accountRepository.findById(id).ifPresentOrElse(account -> {
                    checkAccountOwnership(id, userId);
                    accountRepository.deleteById(id);
                }, () -> {
                    throw new AccountNotFoundException(id);
                }
        );
    }

    private Timestamp getCreationDate() {
        return Timestamp.from(Instant.now());
    }

    private void checkAccountOwnership(UUID accountId, UUID userId) {
        if (!accountRepository.findById(accountId)
                .map(account -> account.getUser() != null && account.getUser().getId().equals(userId))
                .orElse(false)) {
            throw new ForbiddenException("Access denied");
        }
    }
}
