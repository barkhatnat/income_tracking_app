package ru.barkhatnat.income_tracking.service;

import ru.barkhatnat.income_tracking.DTO.AccountDto;
import ru.barkhatnat.income_tracking.DTO.AccountResponseDto;
import ru.barkhatnat.income_tracking.entity.Account;
import ru.barkhatnat.income_tracking.entity.Operation;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

public interface AccountService {
    Iterable<Account> findAllAccounts();

    Iterable<Operation> findAllAccountOperations(Account account);

    AccountResponseDto createAccount(AccountDto accountDto);

    Optional<Account> findAccount(UUID id);

    void updateAccount(UUID id, String title, BigDecimal balance);

    void deleteAccount(UUID id);
}
