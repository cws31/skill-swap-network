package com.skill_swap_network.common.exception;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserAlReadyExistExceeption.class)
    public ResponseEntity<Map<String, Object>> userAlreadyExistExceptionHandler(Exception exp) {
        Map<String, Object> response = new HashMap<>();
        response.put("message", exp.getMessage());
        response.put("error", "user already exist");
        response.put("Status", HttpStatus.CONFLICT);
        response.put("timestamp", LocalDate.now());

        return new ResponseEntity<>(response, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> validationExceptionHandler(MethodArgumentNotValidException exp) {
        Map<String, Object> response = new HashMap<>();
        exp.getBindingResult().getFieldErrors()
                .forEach(error -> response.put(error.getField(), error.getDefaultMessage()));
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(userNotFoundException.class)
    public ResponseEntity<Map<String, Object>> userNotFoundExceptionHandler(Exception exp) {
        Map<String, Object> response = new HashMap<>();
        response.put("message", exp.getMessage());
        response.put("error", "user not registered");
        response.put("status", HttpStatus.NOT_FOUND);
        response.put("timestamp", LocalDate.now());

        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(InvalidCredentialException.class)
    public ResponseEntity<Map<String, Object>> invalidCredentialExceptionHandler(Exception exp) {
        Map<String, Object> response = new HashMap<>();
        response.put("message", exp.getMessage());
        response.put("error", "invalid credential");
        response.put("status", HttpStatus.UNAUTHORIZED);
        response.put("timestamp", LocalDate.now());

        return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
    }

}
