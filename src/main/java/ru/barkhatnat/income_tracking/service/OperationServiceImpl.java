package ru.barkhatnat.income_tracking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.barkhatnat.income_tracking.DTO.OperationDto;
import ru.barkhatnat.income_tracking.DTO.OperationResponseDto;
import ru.barkhatnat.income_tracking.entity.Account;
import ru.barkhatnat.income_tracking.entity.Category;
import ru.barkhatnat.income_tracking.entity.Operation;
import ru.barkhatnat.income_tracking.exception.AccountNotFoundException;
import ru.barkhatnat.income_tracking.exception.CategoryNotFoundException;
import ru.barkhatnat.income_tracking.exception.ForbiddenException;
import ru.barkhatnat.income_tracking.exception.OperationNotFoundException;
import ru.barkhatnat.income_tracking.repositories.OperationRepository;
import ru.barkhatnat.income_tracking.utils.OperationMapper;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OperationServiceImpl implements OperationService {

    private final OperationRepository operationRepository;
    private final OperationMapper operationMapper;
    private final CategoryService categoryService;
    private final AccountService accountService;
    private final BalanceService balanceService;


    @Override
    @Transactional
    public List<Operation> findAllOperationsByAccountId(UUID accountId, UUID userId) {
        checkAccountsOwnership(accountId, userId);
        return operationRepository.findOperationsByAccountId(accountId);
    }

    @Override
    @Transactional
    public OperationResponseDto createOperation(OperationDto operationDto, UUID currentAccountId, UUID userId) {
        UUID categoryId = operationDto.categoryId();
        Category category = categoryService.findCategory(categoryId).orElseThrow(() -> new CategoryNotFoundException(categoryId));
        Account account = accountService.findAccount(currentAccountId).orElseThrow(() -> new AccountNotFoundException(currentAccountId));
        checkAccountsOwnership(account.getId(), userId);
        Operation operation = operationRepository.save(new Operation(operationDto.amount(), operationDto.datePurchase(), category, account, operationDto.note(), getCreationDate()));
        balanceService.calculateAccountBalance(account, operation, userId);
        return operationMapper.toOperationResponseDto(operation);
    }

    @Override
    @Transactional
    public Optional<Operation> findOperation(UUID id, UUID currentAccountId, UUID userId) {
        return operationRepository.findById(id);
    }

    @Override
    @Transactional
    public Optional<Operation> findOperation(UUID id) {
        return operationRepository.findById(id);
    }

    @Override
    @Transactional
    public void updateOperation(UUID id, BigDecimal amount, Timestamp datePurchase, UUID categoryId, String note, UUID currentAccountId, UUID userId) {
        operationRepository.findById(id).ifPresentOrElse(operation -> {
            Account account = accountService.findAccount(currentAccountId).orElseThrow(() -> new AccountNotFoundException(currentAccountId));
            checkAccountsOwnership(currentAccountId, userId);
            checkOperationOwnership(id, currentAccountId);
            Category category = categoryService.findCategory(categoryId).orElseThrow(() -> new CategoryNotFoundException(categoryId));
            operation.setAmount(amount);
            operation.setDatePurchase(datePurchase);
            operation.setCategory(category);
            operation.setNote(note);
            balanceService.calculateAccountBalance(account, operation, userId);
        }, () -> {
            throw new OperationNotFoundException(id);
        });
    }

    @Override
    @Transactional
    public void deleteOperation(UUID id, UUID currentAccountId, UUID userId) {
        operationRepository.findById(id).ifPresentOrElse(operation -> {
            checkAccountsOwnership(currentAccountId, userId);
            checkOperationOwnership(id, currentAccountId);
        }, () -> {
            throw new OperationNotFoundException(id);
        });
    }

    private Timestamp getCreationDate() {
        return Timestamp.from(Instant.now());
    }

    private void checkOperationOwnership(UUID operationId, UUID accountId) {
        if (!operationRepository.findById(operationId)
                .map(operation -> operation.getAccount() != null && operation.getAccount().getId().equals(accountId))
                .orElse(false)) {
            throw new ForbiddenException("Access denied");
        }
    }

    private void checkAccountsOwnership(UUID accountId, UUID userId) {
        if (!accountService.findAccount(accountId)
                .map(account -> account.getUser() != null && account.getUser().getId().equals(userId))
                .orElse(false)) {
            throw new ForbiddenException("Access denied");
        }
    }
}
