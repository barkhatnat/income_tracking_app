package ru.barkhatnat.income_tracking.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.barkhatnat.income_tracking.entity.Account;
import ru.barkhatnat.income_tracking.entity.Operation;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BalanceServiceImpl implements BalanceService {
    private final AccountService accountService;

    @Override
    @Transactional
    public void calculateAccountBalance(Account account, Operation operation, UUID userId) {
        BigDecimal newAccountBalance = operation.getCategory().getCategoryType() ?
                account.getBalance().add(operation.getAmount()) :
                account.getBalance().subtract(operation.getAmount());
        accountService.updateAccount(account.getId(), account.getTitle(), newAccountBalance, userId);
    }
}
