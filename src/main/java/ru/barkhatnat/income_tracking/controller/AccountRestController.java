package ru.barkhatnat.income_tracking.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.barkhatnat.income_tracking.DTO.AccountDto;
import ru.barkhatnat.income_tracking.DTO.AccountResponseDto;
import ru.barkhatnat.income_tracking.entity.Account;
import ru.barkhatnat.income_tracking.service.AccountService;
import ru.barkhatnat.income_tracking.utils.AccountMapper;

import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

@RestController
@PreAuthorize("isAuthenticated()")
@RequiredArgsConstructor
@RequestMapping("/accounts/{accountId:\\d+}") //TODO change pattern for uuid (^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$)
public class AccountRestController {
    private final AccountService accountService;
    private final MessageSource messageSource;
    private final AccountMapper accountMapper;

    @GetMapping
    public ResponseEntity<AccountResponseDto> getAccount(@PathVariable("accountId") UUID accountId) {
        Optional<Account> account = accountService.findAccount(accountId);
        return account.map(value -> ResponseEntity.ok(accountMapper.toAccountResponseDto(value))).orElseGet(() -> ResponseEntity.notFound().build());
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
            this.accountService.updateAccount(accountId, accountDto.title(), accountDto.balance());
            return ResponseEntity.noContent().build();
        }
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteAccount(@PathVariable("accountId") UUID accountId) {
        this.accountService.deleteAccount(accountId);
        return ResponseEntity.noContent().build();
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<ProblemDetail> handleNoSuchElementException(NoSuchElementException e, Locale locale) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND,
                        this.messageSource.getMessage(e.getMessage(), new Object[0],
                                e.getMessage(), locale)));
    }
}
