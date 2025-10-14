package com.codewithdevil.bank.controllers;

import com.codewithdevil.bank.exceptions.JwtInvalidException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler({MethodArgumentNotValidException.class})
    public ResponseEntity<Map<String, String>> validationErrorHandler(
            MethodArgumentNotValidException exception
    ){
        var errors = new HashMap<String, String>();

        exception.getBindingResult().getFieldErrors().forEach((error) -> {
            errors.put(error.getField(), error.getDefaultMessage());
        });

        return ResponseEntity.badRequest().body(errors);
    }

    @ExceptionHandler(JwtInvalidException.class)
    public ResponseEntity<Map<String, String>> handleJwtInvalidException(){
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                Map.of("Error", "Token is null or expired")
        );
    }
}
