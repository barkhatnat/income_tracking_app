package ru.barkhatnat.income_tracking.service;

import ru.barkhatnat.income_tracking.entity.Account;
import ru.barkhatnat.income_tracking.entity.Operation;

import java.util.UUID;

public interface BalanceService {
    void calculateAccountBalance(Account account, Operation operation, UUID userId);
}
