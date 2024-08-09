package ru.barkhatnat.income_tracking.listener;

import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import ru.barkhatnat.income_tracking.entity.Account;
import ru.barkhatnat.income_tracking.entity.Operation;
import ru.barkhatnat.income_tracking.event.OperationCreatedEvent;
import ru.barkhatnat.income_tracking.event.OperationDeletedEvent;
import ru.barkhatnat.income_tracking.event.OperationUpdatedEvent;
import ru.barkhatnat.income_tracking.service.BalanceService;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
public class OperationEventListener {
    private final BalanceService balanceService;

    @EventListener
    public void handleOperationCreated(OperationCreatedEvent event) {
        Operation operation = event.getOperation();
        changeAccountBalance(operation.getAccount(), operation.getAmount(), operation.getCategory().getCategoryType());
    }

    @EventListener
    public void handleOperationUpdated(OperationUpdatedEvent event) {
        Operation newOperation = event.getNewOperation();
        BigDecimal difference = event.getDifference();
        changeAccountBalance(newOperation.getAccount(), difference, newOperation.getCategory().getCategoryType());
    }

    @EventListener
    public void handleOperationDeleted(OperationDeletedEvent event) {
        Operation operation = event.getOperation();
        changeAccountBalance(operation.getAccount(), operation.getAmount(), !operation.getCategory().getCategoryType());
    }

    private void changeAccountBalance(Account account, BigDecimal amount, Boolean categoryType) {
        balanceService.changeAccountBalanceByDifference(account, amount, categoryType);
    }
}
