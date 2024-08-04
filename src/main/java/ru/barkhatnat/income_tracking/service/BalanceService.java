package ru.barkhatnat.income_tracking.service;

import ru.barkhatnat.income_tracking.entity.Account;
import ru.barkhatnat.income_tracking.entity.Operation;

public interface BalanceService {

    void establishAccountBalance(Account account, Operation operation);

    void cancelAccountBalance(Account account, Operation operation);
}
