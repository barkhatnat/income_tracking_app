package ru.barkhatnat.income_tracking.service;

import ru.barkhatnat.income_tracking.DTO.AccountDto;
import ru.barkhatnat.income_tracking.DTO.AccountResponseDto;
import ru.barkhatnat.income_tracking.entity.Account;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AccountService {
    List<Account> findAllAccountsByUserId(UUID userId);

    AccountResponseDto createAccount(AccountDto accountDto, UUID userId);

    Optional<Account> findAccount(UUID id, UUID userId);

    Optional<Account> findAccount(UUID id);

    void updateAccount(UUID id, String title, BigDecimal balance, UUID userId);

    void deleteAccount(UUID id, UUID userId);
}
