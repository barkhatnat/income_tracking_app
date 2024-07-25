package ru.barkhatnat.income_tracking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.barkhatnat.income_tracking.DTO.OperationDto;
import ru.barkhatnat.income_tracking.DTO.OperationResponseDto;
import ru.barkhatnat.income_tracking.entity.Account;
import ru.barkhatnat.income_tracking.entity.Category;
import ru.barkhatnat.income_tracking.entity.Operation;
import ru.barkhatnat.income_tracking.repositories.OperationRepository;
import ru.barkhatnat.income_tracking.utils.OperationMapper;
import ru.barkhatnat.income_tracking.utils.SecurityUtil;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OperationServiceImpl implements OperationService {

    private final OperationRepository operationRepository;
    private final OperationMapper operationMapper;
    private final CategoryService categoryService;
    private final AccountService accountService;
    private final SecurityUtil securityUtil;


    @Override
    @Transactional
    public Iterable<Operation> findAllOperations() {
        return operationRepository.findAll();
    }

    @Override
    @Transactional
    public Iterable<Operation> findAllAccountOperations(UUID accountId) {
        Optional<Account> account = accountService.findAccount(accountId);
        if (account.isEmpty()) {
            throw new NoSuchElementException(); //TODO сделать кастомный эксепшн
        }
        if (!account.get().getUser().getId().equals(securityUtil.getCurrentUserDetails().getUserId())) {
            throw new IllegalArgumentException("You do not have permission to create operation for this account.");
        }
        return operationRepository.findOperationsByAccountId(accountId);
    }

    @Override
    @Transactional
    public OperationResponseDto createOperation(OperationDto operationDto, UUID currentAccountId) {
        Optional<Category> category = categoryService.findCategory(operationDto.categoryId());
        if (category.isEmpty()) {
            throw new NoSuchElementException(); //TODO сделать кастомный эксепшн
        }
        Optional<Account> account = accountService.findAccount(currentAccountId);
        if (account.isEmpty()) {
            throw new NoSuchElementException(); //TODO сделать кастомный эксепшн
        }
        if (!account.get().getUser().getId().equals(securityUtil.getCurrentUserDetails().getUserId())) {
            throw new IllegalArgumentException("You do not have permission to create operation for this account.");
        }
        Operation operation = operationRepository.save(new Operation(operationDto.amount(), operationDto.datePurchase(), category.get(), account.get(), operationDto.note(), getCreationDate()));
        return operationMapper.toOperationResponseDto(operation);

    }

    @Override
    @Transactional
    public Optional<Operation> findOperation(UUID id) {
        return operationRepository.findById(id);
    }

    @Override
    @Transactional
    public void updateOperation(UUID id, BigDecimal amount, Timestamp datePurchase, UUID categoryId, String note, UUID currentAccountId) {
        operationRepository.findById(id).ifPresentOrElse(operation -> {
            if (operation.getAccount() != null && operation.getAccount().getId().equals(currentAccountId)) {
                Optional<Category> category = categoryService.findCategory(categoryId);
                if (category.isEmpty()) {
                    throw new NoSuchElementException(); //TODO сделать кастомный эксепшн
                }
                operation.setAmount(amount);
                operation.setDatePurchase(datePurchase);
                operation.setCategory(category.get());
                operation.setNote(note);
            } else {
                throw new IllegalArgumentException("You do not have permission to update this operation.");
            }
        }, () -> {
            throw new NoSuchElementException();
        });
    }

    @Override
    @Transactional
    public void deleteOperation(UUID id, UUID currentAccountId) {
        operationRepository.findById(id).ifPresentOrElse(operation -> {
            if (operation.getAccount() != null && operation.getAccount().getId().equals(currentAccountId)) {
                operationRepository.deleteById(id);
            } else {
                throw new IllegalArgumentException("You do not have permission to update this operation.");
            }
        }, () -> {
            throw new NoSuchElementException();
        });
    }

    private Timestamp getCreationDate() {
        return Timestamp.from(Instant.now());
    }
}
