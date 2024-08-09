package ru.barkhatnat.income_tracking.exception_handling;

import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import ru.barkhatnat.income_tracking.exception.CategoryTypeException;
import ru.barkhatnat.income_tracking.exception.UserAlreadyExistsException;

import java.util.List;
import java.util.Locale;

@ControllerAdvice
@RequiredArgsConstructor
public class BadRequestControllerAdvice {
    private final MessageSource messageSource;

    @ExceptionHandler(BindException.class)
    public ResponseEntity<ProblemDetail> handleBindException(BindException e, Locale locale) {
        List<String> errors = e.getAllErrors().stream()
                .map(error -> messageSource.getMessage(error, locale))
                .toList();

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST,
                messageSource.getMessage("validation.error.message", null, locale) + " " +
                        messageSource.getMessage("validation.error.details", null, locale));

        problemDetail.setProperty("error", errors);
        return ResponseEntity.badRequest().body(problemDetail);
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<ProblemDetail> handleUserAlreadyExistsException(UserAlreadyExistsException e, Locale locale) {
        String message = messageSource.getMessage(e.getMessage(), new Object[0], e.getMessage(), locale);
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, message);
        return ResponseEntity.status(HttpStatus.CONFLICT).body(problemDetail);
    }

    @ExceptionHandler(CategoryTypeException.class)
    public ResponseEntity<ProblemDetail> handleCategoryTypeException(CategoryTypeException e, Locale locale) {
        String message = messageSource.getMessage(e.getMessage(), new Object[0], e.getMessage(), locale);
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, message);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(problemDetail);
    }
}
