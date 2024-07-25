package ru.barkhatnat.income_tracking.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.barkhatnat.income_tracking.DTO.OperationDto;
import ru.barkhatnat.income_tracking.service.OperationService;

import java.util.UUID;

@RestController
@PreAuthorize("isAuthenticated()")
@RequestMapping("/accounts/{accountId:\\d+}/operations/{operationId:\\d+}") //TODO change pattern for uuid
@RequiredArgsConstructor
public class OperationRestController {
    private final OperationService operationService;

    @PatchMapping
    public ResponseEntity<?> updateOperation(@PathVariable("operationId") UUID operationId,
                                             @PathVariable("accountId") UUID accountId,
                                             @Valid @RequestBody OperationDto operationDto,
                                             BindingResult bindingResult) throws BindException {
        if (bindingResult.hasErrors()) {
            if (bindingResult instanceof BindException exception) {
                throw exception;
            } else {
                throw new BindException(bindingResult);
            }
        } else {
            operationService.updateOperation(operationId, operationDto.amount(), operationDto.datePurchase(), operationDto.categoryId(), operationDto.note(), accountId);
            return ResponseEntity.noContent().build();
        }
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteCategory(@PathVariable("operationId") UUID operationId, @PathVariable("accountId") UUID accountId) {
        operationService.deleteOperation(operationId, accountId);
        return ResponseEntity.noContent().build();
    }
}
