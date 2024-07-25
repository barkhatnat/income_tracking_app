package ru.barkhatnat.income_tracking.service;

import ru.barkhatnat.income_tracking.DTO.AccountDto;
import ru.barkhatnat.income_tracking.DTO.AccountResponseDto;
import ru.barkhatnat.income_tracking.entity.Account;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AccountService {
    List<Account> findAllAccountsByUserId();

    AccountResponseDto createAccount(AccountDto accountDto);

    Optional<Account> findAccount(UUID id);

    void updateAccount(UUID id, String title, BigDecimal balance);

    void deleteAccount(UUID id);
}
