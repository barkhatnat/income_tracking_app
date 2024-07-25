package ru.barkhatnat.income_tracking.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import ru.barkhatnat.income_tracking.DTO.AccountDto;
import ru.barkhatnat.income_tracking.DTO.AccountResponseDto;
import ru.barkhatnat.income_tracking.entity.Account;
import ru.barkhatnat.income_tracking.service.AccountService;
import ru.barkhatnat.income_tracking.utils.AccountMapper;
import ru.barkhatnat.income_tracking.utils.SecurityUtil;

import java.util.Map;
import java.util.UUID;

@RestController
@PreAuthorize("isAuthenticated()")
@RequiredArgsConstructor
@RequestMapping("/accounts")
public class AccountsRestController {
    private final AccountService accountService;
    private final AccountMapper accountMapper;
    private final SecurityUtil securityUtil;

    @GetMapping
    public ResponseEntity<Iterable<AccountResponseDto>> getAccountsList() {
        UUID currentUserId = securityUtil.getCurrentUserDetails().getUserId();
        Iterable<Account> accounts = accountService.findAllAccountsByUserId(currentUserId);
        Iterable<AccountResponseDto> userResponseCollection = accountMapper.toAccountResponseDtoCollection(accounts);
        return ResponseEntity.ok(userResponseCollection);
    }

    @PostMapping
    public ResponseEntity<?> createAccount(@Valid @RequestBody AccountDto accountDto,
                                           BindingResult bindingResult,
                                           UriComponentsBuilder uriComponentsBuilder) throws BindException {
        if (bindingResult.hasErrors()) {
            if (bindingResult instanceof BindException exception) {
                throw exception;
            } else {
                throw new BindException(bindingResult);
            }
        } else {
            UUID currentUserId = securityUtil.getCurrentUserDetails().getUserId();
            AccountResponseDto accountResponseDto = accountService.createAccount(accountDto, currentUserId);
            return ResponseEntity.created(uriComponentsBuilder
                            .replacePath("/accounts/{accountId}")
                            .build(Map.of("accountId", accountResponseDto.id())))
                    .body(accountResponseDto);
        }
    }
}
