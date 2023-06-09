package com.epam.esm.epammodule4.controller.advice;

import com.epam.esm.epammodule4.exception.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Slf4j
@ControllerAdvice
public class ApplicationControllerAdvice {

    @ExceptionHandler(TagNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleTagNotFound(TagNotFoundException exception) {
        log.warn("Cannot find tag: {}", exception.getMessage());
        ErrorResponse responseBody = ErrorResponse.of(exception.getMessage(), 40401);

        return ResponseEntity.status(NOT_FOUND).body(responseBody);
    }

    @ExceptionHandler(TagAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleTagAlreadyExists(TagAlreadyExistsException exception) {
        log.warn("Tag already exists: {}", exception.getMessage());
        ErrorResponse responseBody = ErrorResponse.of(exception.getMessage(), 40901);

        return ResponseEntity.status(CONFLICT).body(responseBody);
    }

    @ExceptionHandler(GiftCertificateNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleGiftCertificateNotFound(GiftCertificateNotFoundException exception) {
        log.warn("Cannot find certificate: {}", exception.getMessage());
        ErrorResponse responseBody = ErrorResponse.of(exception.getMessage(), 40402);

        return ResponseEntity.status(NOT_FOUND).body(responseBody);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleGiftCertificateNotFound(UserNotFoundException exception) {
        log.warn("Cannot find user: {}", exception.getMessage());
        ErrorResponse responseBody = ErrorResponse.of(exception.getMessage(), 40403);

        return ResponseEntity.status(NOT_FOUND).body(responseBody);
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleUserAlreadyExists(UserAlreadyExistsException exception) {
        log.warn("User already exists: {}", exception.getMessage());
        ErrorResponse responseBody = ErrorResponse.of(exception.getMessage(), 40903);

        return ResponseEntity.status(CONFLICT).body(responseBody);
    }

    @ExceptionHandler(UserIdIncorrectException.class)
    public ResponseEntity<ErrorResponse> handleUserIdIncorrect(UserIdIncorrectException exception) {
        log.warn("User id belongs to another user: {}", exception.getMessage());
        ErrorResponse responseBody = ErrorResponse.of(exception.getMessage(), 41003);

        return ResponseEntity.status(CONFLICT).body(responseBody);
    }

    @ExceptionHandler(UserCannotDeleteException.class)
    public ResponseEntity<ErrorResponse> handleUserCannotDeleteException(UserCannotDeleteException exception) {
        log.warn("User cannot be deleted {}", exception.getMessage());
        ErrorResponse responseBody = ErrorResponse.of(exception.getMessage(), 41103);

        return ResponseEntity.status(CONFLICT).body(responseBody);
    }

    @ExceptionHandler(UsernameAlreadyTakenException.class)
    public ResponseEntity<ErrorResponse> handleUsernameAlreadyTakenException(UsernameAlreadyTakenException exception) {
        log.warn("Username is already taken: {}", exception.getMessage());
        ErrorResponse responseBody = ErrorResponse.of(exception.getMessage(), 41203);

        return ResponseEntity.status(CONFLICT).body(responseBody);
    }

    @ExceptionHandler(EmailAlreadyInUseException.class)
    public ResponseEntity<ErrorResponse> handleEmailAlreadyInUseException(EmailAlreadyInUseException exception) {
        log.warn("Email is already in use: {}", exception.getMessage());
        ErrorResponse responseBody = ErrorResponse.of(exception.getMessage(), 41303);

        return ResponseEntity.status(CONFLICT).body(responseBody);
    }

    @ExceptionHandler(OrderNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleOrderNotFound(OrderNotFoundException exception) {
        log.warn("Cannot find order: {}", exception.getMessage());
        ErrorResponse responseBody = ErrorResponse.of(exception.getMessage(), 40404);

        return ResponseEntity.status(NOT_FOUND).body(responseBody);
    }

    @ExceptionHandler(OrderAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleOrderAlreadyExists(OrderAlreadyExistsException exception) {
        log.warn("Order already exists: {}", exception.getMessage());
        ErrorResponse responseBody = ErrorResponse.of(exception.getMessage(), 40904);

        return ResponseEntity.status(CONFLICT).body(responseBody);
    }

    @ExceptionHandler(TokenRefreshException.class)
    public ResponseEntity<ErrorResponse> handleTokenRefreshException(TokenRefreshException exception, WebRequest request) {
        log.warn("Token refresh error: {} {}", exception.getMessage(), request.getDescription(false));
        ErrorResponse responseBody = ErrorResponse.of(exception.getMessage(), 40905);

        return ResponseEntity.status(CONFLICT).body(responseBody);
    }
}
