package ru.barkhatnat.income_tracking.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.barkhatnat.income_tracking.entity.Account;
import ru.barkhatnat.income_tracking.repositories.AccountRepository;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class BalanceServiceImpl implements BalanceService {
    private final AccountRepository accountRepository;

    @Override
    @Transactional
    public void changeAccountBalanceByDifference(Account account, BigDecimal difference, Boolean categoryType) {
        BigDecimal newAccountBalance = categoryType ?
                account.getBalance().add(difference) :
                account.getBalance().subtract(difference);
        accountRepository.updateAccountBalance(account.getId(), newAccountBalance);
        accountRepository.save(account);
    }
}
