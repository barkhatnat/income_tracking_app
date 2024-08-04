package ru.barkhatnat.income_tracking.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;
import ru.barkhatnat.income_tracking.entity.Operation;

@Getter
public class OperationDeletedEvent extends ApplicationEvent {
    private final Operation operation;

    public OperationDeletedEvent(Object source, Operation operation) {
        super(source);
        this.operation = operation;
    }
}
