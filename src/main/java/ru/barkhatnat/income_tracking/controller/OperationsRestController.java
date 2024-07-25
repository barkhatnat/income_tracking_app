package ru.barkhatnat.income_tracking.controller;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import ru.barkhatnat.income_tracking.DTO.OperationDto;
import ru.barkhatnat.income_tracking.DTO.OperationResponseDto;
import ru.barkhatnat.income_tracking.entity.Operation;
import ru.barkhatnat.income_tracking.service.OperationService;
import ru.barkhatnat.income_tracking.utils.OperationMapper;
import ru.barkhatnat.income_tracking.utils.SecurityUtil;

import java.util.Map;
import java.util.UUID;

@RestController
@PreAuthorize("isAuthenticated()")
@RequestMapping("/accounts/{accountId:[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}}/operations")
@RequiredArgsConstructor
public class OperationsRestController {
    private final OperationService operationService;
    private final OperationMapper operationMapper;
    private final SecurityUtil securityUtil;

    @GetMapping
    public ResponseEntity<Iterable<OperationResponseDto>> getOperationList(@PathVariable("accountId") UUID accountId) {
        UUID currentUserId = securityUtil.getCurrentUserDetails().getUserId();
        Iterable<Operation> operations = operationService.findAllOperationsByAccountId(accountId, currentUserId);
        Iterable<OperationResponseDto> operationResponseDto = operationMapper.toOperationResponseDtoCollection(operations);
        return ResponseEntity.ok(operationResponseDto);
    }

    @PostMapping
    public ResponseEntity<?> createOperation(@Valid @RequestBody OperationDto operationDto,
                                             BindingResult bindingResult,
                                             UriComponentsBuilder uriComponentsBuilder, @PathVariable String accountId) throws BindException {
        if (bindingResult.hasErrors()) {
            if (bindingResult instanceof BindException exception) {
                throw exception;
            } else {
                throw new BindException(bindingResult);
            }
        } else {
            UUID currentUserId = securityUtil.getCurrentUserDetails().getUserId();
            OperationResponseDto operationResponseDto = operationService.createOperation(operationDto, UUID.fromString(accountId), currentUserId);
            return ResponseEntity.created(uriComponentsBuilder
                            .replacePath("/accounts/{accountId}/operations/{operationId}")
                            .build(Map.of("accountId", accountId, "operationId", operationResponseDto.id())))
                    .body(operationResponseDto);
        }
    }
}
