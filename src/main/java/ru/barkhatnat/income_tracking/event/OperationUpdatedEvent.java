package ru.barkhatnat.income_tracking.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;
import ru.barkhatnat.income_tracking.entity.Operation;

import java.math.BigDecimal;

@Getter
public class OperationUpdatedEvent extends ApplicationEvent {
    private final Operation newOperation;
    private final BigDecimal difference;

    public OperationUpdatedEvent(Object source, Operation newOperation, BigDecimal difference) {
        super(source);
        this.difference = difference;
        this.newOperation = newOperation;
    }
}
