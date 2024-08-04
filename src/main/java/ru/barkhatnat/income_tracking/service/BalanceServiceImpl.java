package ru.barkhatnat.income_tracking.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.barkhatnat.income_tracking.entity.Account;
import ru.barkhatnat.income_tracking.entity.Operation;
import ru.barkhatnat.income_tracking.repositories.AccountRepository;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class BalanceServiceImpl implements BalanceService {
    private final AccountRepository accountRepository;

    @Override
    @Transactional
    public void establishAccountBalance(Account account, Operation operation) {
        BigDecimal newAccountBalance = operation.getCategory().getCategoryType() ?
                account.getBalance().add(operation.getAmount()) :
                account.getBalance().subtract(operation.getAmount());
        accountRepository.updateAccountBalance(account.getId(), newAccountBalance);
    }

    @Override
    @Transactional
    public void cancelAccountBalance(Account account, Operation operation) {
        BigDecimal newAccountBalance = operation.getCategory().getCategoryType() ?
                account.getBalance().subtract(operation.getAmount()) :
                account.getBalance().add(operation.getAmount());
        accountRepository.updateAccountBalance(account.getId(), newAccountBalance);
    }
}
