package com.asr.bank.error.Exceptions;

import com.asr.bank.error.ApiError;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    @ExceptionHandler(AccountNotFoundException.class)
    public ResponseEntity<ApiError> handleAccountNotFound(AccountNotFoundException ex) {
        ApiError error = new ApiError("ACCOUNT_NOT_FOUND", ex.getMessage());
        log.error("ACCOUNT_NOT_FOUND", ex.getMessage());
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(WithdrawalLimitExceededException.class)
    public ResponseEntity<ApiError> handleWithdrawalLimitExceeded(WithdrawalLimitExceededException ex) {
        ApiError error = new ApiError("WITHDRAWAL_LIMIT_EXCEEDED", ex.getMessage());
        log.error("WITHDRAWAL_LIMIT_EXCEEDED", ex.getMessage());
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleGeneric(Exception ex) {
        ApiError error = new ApiError("INTERNAL_ERROR", "An unexpected error occurred");
        log.error("INTERNAL_ERROR", ex.getMessage());
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
