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
import ru.barkhatnat.income_tracking.utils.SecurityUtil;

import java.util.UUID;

@RestController
@PreAuthorize("isAuthenticated()")
@RequestMapping("/accounts/{accountId:[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}}/operations/{operationId:[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}}")
@RequiredArgsConstructor
public class OperationRestController {
    private final OperationService operationService;
    private final SecurityUtil securityUtil;

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
            UUID currentUserId = securityUtil.getCurrentUserDetails().getUserId();
            operationService.updateOperation(operationId, operationDto.amount(), operationDto.datePurchase(), operationDto.categoryId(), operationDto.note(), accountId, currentUserId);
            return ResponseEntity.noContent().build();
        }
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteCategory(@PathVariable("operationId") UUID operationId, @PathVariable("accountId") UUID accountId) {
        UUID currentUserId = securityUtil.getCurrentUserDetails().getUserId();
        operationService.deleteOperation(operationId, accountId, currentUserId);
        return ResponseEntity.noContent().build();
    }
}
