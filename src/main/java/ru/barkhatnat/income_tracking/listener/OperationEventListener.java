package ru.barkhatnat.income_tracking.listener;

import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import ru.barkhatnat.income_tracking.event.OperationCreatedEvent;
import ru.barkhatnat.income_tracking.event.OperationDeletedEvent;
import ru.barkhatnat.income_tracking.event.OperationUpdatedEvent;
import ru.barkhatnat.income_tracking.service.BalanceService;

@Component
@RequiredArgsConstructor
public class OperationEventListener {
    private final BalanceService balanceService;

    @EventListener
    public void handleOperationCreated(OperationCreatedEvent event) {
        balanceService.establishAccountBalance(event.getOperation().getAccount(), event.getOperation());
    }

    @EventListener
    public void handleOperationUpdated(OperationUpdatedEvent event) {
        balanceService.cancelAccountBalance(event.getOldOperation().getAccount(), event.getOldOperation());
        balanceService.cancelAccountBalance(event.getNewOperation().getAccount(), event.getNewOperation());
    }

    @EventListener
    public void handleOperationDeleted(OperationDeletedEvent event) {
        balanceService.cancelAccountBalance(event.getOperation().getAccount(), event.getOperation());
    }
}
