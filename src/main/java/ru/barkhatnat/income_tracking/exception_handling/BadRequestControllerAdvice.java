package ru.barkhatnat.income_tracking.exception_handling;

import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import ru.barkhatnat.income_tracking.exception.UserAlreadyExistsException;

import java.util.Locale;

@ControllerAdvice
@RequiredArgsConstructor
public class BadRequestControllerAdvice {
    private final MessageSource messageSource;

    @ResponseStatus(code = HttpStatus.BAD_REQUEST)
    @ExceptionHandler(BindException.class)
    public ResponseEntity<ProblemDetail> handleBindException(BindException e, Locale locale) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST,
                this.messageSource.getMessage(e.getMessage(), new Object[0],
                        e.getMessage(), locale));
        problemDetail.setProperty("error",
                e.getAllErrors().stream()
                        .map(ObjectError::getDefaultMessage)
                        .toList());
        return ResponseEntity.badRequest().body(problemDetail);
    }

    @ResponseStatus(code = HttpStatus.BAD_REQUEST)
    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<ProblemDetail> handleUserAlreadyExistsException(UserAlreadyExistsException e, Locale locale) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST,
                messageSource.getMessage(e.getMessage(), new Object[0], e.getMessage(), locale)
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(problemDetail);
    }

}
