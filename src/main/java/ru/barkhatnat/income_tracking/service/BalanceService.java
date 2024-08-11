package ru.barkhatnat.income_tracking.service;

import ru.barkhatnat.income_tracking.entity.Account;

import java.math.BigDecimal;

public interface BalanceService {
    void changeAccountBalanceByDifference(Account account, BigDecimal difference, Boolean categoryType);
}
