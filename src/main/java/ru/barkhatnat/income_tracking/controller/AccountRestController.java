package ru.barkhatnat.income_tracking.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.barkhatnat.income_tracking.DTO.AccountDto;
import ru.barkhatnat.income_tracking.DTO.AccountResponseDto;
import ru.barkhatnat.income_tracking.entity.Account;
import ru.barkhatnat.income_tracking.exception.AccountNotFoundException;
import ru.barkhatnat.income_tracking.service.AccountService;
import ru.barkhatnat.income_tracking.utils.AccountMapper;
import ru.barkhatnat.income_tracking.utils.SecurityUtil;

import java.util.Optional;
import java.util.UUID;

@RestController
@PreAuthorize("isAuthenticated()")
@RequiredArgsConstructor
@RequestMapping("/accounts/{accountId:[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}}")
public class AccountRestController {
    private final AccountService accountService;
    private final AccountMapper accountMapper;
    private final SecurityUtil securityUtil;

    @GetMapping
    public ResponseEntity<AccountResponseDto> getAccount(@PathVariable("accountId") UUID accountId) {
        UUID currentUserId = securityUtil.getCurrentUserDetails().getUserId();
        Optional<Account> account = accountService.findAccount(accountId, currentUserId);
        return account.map(value -> ResponseEntity.ok(accountMapper.toAccountResponseDto(value))).orElseThrow(() -> new AccountNotFoundException(accountId));
    }

    @PatchMapping
    public ResponseEntity<?> updateAccount(@PathVariable("accountId") UUID accountId, @Valid @RequestBody AccountDto accountDto,
                                           BindingResult bindingResult) throws BindException {
        if (bindingResult.hasErrors()) {
            if (bindingResult instanceof BindException exception) {
                throw exception;
            } else {
                throw new BindException(bindingResult);
            }
        } else {
            UUID currentUserId = securityUtil.getCurrentUserDetails().getUserId();
            this.accountService.updateAccount(accountId, accountDto.title(), accountDto.balance(), currentUserId);
            return ResponseEntity.noContent().build();
        }
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteAccount(@PathVariable("accountId") UUID accountId) {
        UUID currentUserId = securityUtil.getCurrentUserDetails().getUserId();
        this.accountService.deleteAccount(accountId, currentUserId);
        return ResponseEntity.noContent().build();
    }
}
