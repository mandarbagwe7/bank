package com.codewithdevil.bank.controllers;

import com.codewithdevil.bank.dtos.AccountRequest;
import com.codewithdevil.bank.dtos.AccountStatusRequest;
import com.codewithdevil.bank.exceptions.AccountNotFoundException;
import com.codewithdevil.bank.exceptions.CustomerKYCInvalidException;
import com.codewithdevil.bank.exceptions.CustomerNotFoundException;
import com.codewithdevil.bank.services.AccountService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@AllArgsConstructor
@RequestMapping("/accounts")
public class AccountController {

    private final AccountService accountService;

    @PostMapping
    public ResponseEntity<?> createAccount(
            @Valid @RequestBody AccountRequest request,
            @RequestHeader("Authorization") String token
    ){
        var response = accountService.createAccount(request, token);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<?> updateAccountStatus(
            @PathVariable Long id,
            @RequestHeader("Authorization") String token,
            @Valid @RequestBody AccountStatusRequest request
    ){
        var response = accountService.updateAccountStatus(id, token, request);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @ExceptionHandler(CustomerNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleCustomerNotFoundException(){
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                Map.of("Error", "Customer not found")
        );
    }

    @ExceptionHandler(CustomerKYCInvalidException.class)
    public ResponseEntity<Map<String, String>> handleCustomerKYCInvalidException(){
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                Map.of("Error", "Customer Kyc status is not active")
        );
    }

    @ExceptionHandler(AccountNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleAccountNotFoundException(){
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                Map.of("Error", "Account not found")
        );
    }

}
