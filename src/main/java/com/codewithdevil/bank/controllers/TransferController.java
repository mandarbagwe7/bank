package com.codewithdevil.bank.controllers;

import com.codewithdevil.bank.dtos.TransferRequest;
import com.codewithdevil.bank.entities.*;
import com.codewithdevil.bank.exceptions.*;
import com.codewithdevil.bank.services.TransferService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@AllArgsConstructor
@RequestMapping("/transfers")
public class TransferController {

    private final TransferService transferService;

    @PostMapping
    public ResponseEntity<?> transferMoney(
            @RequestBody TransferRequest request,
            @RequestHeader("Authorization") String token,
            @RequestHeader("Idempotency-Key") String idempotencyKey
    ){
        var response = transferService.transferMoney(request, token, idempotencyKey);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @ExceptionHandler(AccountNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleAccountNotFoundException(AccountNotFoundException e){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                Map.of("Error", e.getMessage())
        );
    }

    @ExceptionHandler(UnauthorizeCustomerException.class)
    public ResponseEntity<Map<String, String>> handleUnauthorizeCustomerException(){
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                Map.of("Error", "Unauthorized Customer")
        );
    }

    @ExceptionHandler(AccountStatusInactiveException.class)
    public ResponseEntity<Map<String, String>> handleAccountStatusInactiveException(AccountStatusInactiveException e){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                Map.of("Error", e.getMessage())
        );
    }

    @ExceptionHandler(CurrencyMismatchException.class)
    public ResponseEntity<Map<String, String>> handleCurrencyMismatchException(CurrencyMismatchException e){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                Map.of("Error", e.getMessage())
        );
    }

    @ExceptionHandler(LowBalanceException.class)
    public ResponseEntity<Map<String, String>> handleLowBalanceException(){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                Map.of("Error", "Source account balance not enough")
        );
    }
}
