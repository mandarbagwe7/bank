package com.codewithdevil.bank.controllers;

import com.codewithdevil.bank.dtos.CustomerRequest;
import com.codewithdevil.bank.dtos.KycStatusRequest;
import com.codewithdevil.bank.exceptions.CustomerConflictException;
import com.codewithdevil.bank.exceptions.CustomerNotFoundException;
import com.codewithdevil.bank.exceptions.UserNotFoundException;
import com.codewithdevil.bank.services.CustomerService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@AllArgsConstructor
@RequestMapping("/customers")
public class CustomerController {

    private final CustomerService customerService;

    @PostMapping
    public ResponseEntity<?> createCustomer(
            @RequestBody CustomerRequest request,
            @RequestHeader(name = "Authorization") String token
    ){
        var response = customerService.createCustomer(request, token);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/me")
    public ResponseEntity<?> getSelf(
            @RequestHeader(name = "Authorization") String token
    ){
        var response = customerService.getSelf(token);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<?> updateCustomer(
            @PathVariable long id,
            @RequestBody KycStatusRequest request,
            @RequestHeader(name = "Authorization") String token
    ){
        var response = customerService.updateCustomer(id, request, token);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleUserNotFoundException(){
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    @ExceptionHandler(CustomerConflictException.class)
    public ResponseEntity<Map<String, String>> handleCustomerConflictException(){
        return ResponseEntity.status(HttpStatus.CONFLICT).body(
                Map.of("Error", "Customer already exists")
        );
    }

    @ExceptionHandler(CustomerNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleCustomerNotFoundException(){
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
}
