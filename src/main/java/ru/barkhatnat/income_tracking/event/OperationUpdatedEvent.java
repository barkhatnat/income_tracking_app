package ru.barkhatnat.income_tracking.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;
import ru.barkhatnat.income_tracking.entity.Operation;

@Getter
public class OperationUpdatedEvent extends ApplicationEvent {
    private final Operation oldOperation;
    private final Operation newOperation;

    public OperationUpdatedEvent(Object source, Operation oldOperation, Operation newOperation) {
        super(source);
        this.oldOperation = oldOperation;
        this.newOperation = newOperation;
    }
}
