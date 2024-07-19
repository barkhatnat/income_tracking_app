package ru.barkhatnat.income_tracking.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.barkhatnat.income_tracking.entity.Operation;

import java.util.UUID;

@Repository
public interface OperationRepository extends JpaRepository<Operation, UUID> {
    Iterable<Operation> findOperationsByAccount_Id(UUID account_id);
}
