package com.codewithdevil.bank.controllers;

import com.codewithdevil.bank.dtos.AccountRequest;
import com.codewithdevil.bank.dtos.AccountResponse;
import com.codewithdevil.bank.dtos.AccountStatusRequest;
import com.codewithdevil.bank.dtos.AccountStatusResponse;
import com.codewithdevil.bank.entities.Account;
import com.codewithdevil.bank.entities.AccountStatus;
import com.codewithdevil.bank.entities.KycStatus;
import com.codewithdevil.bank.repositories.AccountRepository;
import com.codewithdevil.bank.repositories.CustomerRepository;
import io.jsonwebtoken.impl.security.EdwardsCurve;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

@RestController
@AllArgsConstructor
@RequestMapping("/accounts")
public class AccountController {

    private final AccountRepository accountRepository;
    private JwtController jwtController;
    private CustomerRepository customerRepository;

    private static Long accountCount = 0L;

    @PostMapping
    public ResponseEntity<?> createAccount(
            @RequestBody AccountRequest request,
            @RequestHeader("Authorization") String token
    ){
        var jwt = jwtController.getJwt(token);
        if(jwt.isExpired()){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                    Map.of("Error", "Token is expired")
            );
        }

        var customer = customerRepository.findByUserId(Long.parseLong(jwt.getUserId())).orElse(null);
        if(customer == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    Map.of("Error", "Customer not found")
            );
        }

        if(!customer.getKycStatus().equals(KycStatus.ACTIVE)){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                    Map.of("Error", "Customer Kyc status is not active")
            );
        }

        var account = Account.builder()
                        .accountNumber(getAccountNumber())
                        .type(request.getType())
                        .currency(request.getCurrency())
                        .balance(BigDecimal.ZERO)
                        .status(AccountStatus.ACTIVE)
                        .customer(customer)
                        .version(1)
                        .openAt(LocalDateTime.now())
                        .createdAt(LocalDateTime.now())
                        .updatedAt(LocalDateTime.now())
                        .build();

        accountRepository.save(account);

        return ResponseEntity.status(HttpStatus.CREATED).body(
                new AccountResponse(
                        account.getId(),
                        account.getStatus(),
                        account.getBalance()
                )
        );

    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<?> updateAccountStatus(
            @PathVariable Long id,
            @RequestHeader("Authorization") String token,
            @RequestBody AccountStatusRequest request
    ){
        var jwt = jwtController.getJwt(token);
        if(jwt.isExpired()){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                    Map.of("Error", "Token is expired")
            );
        }

        var account = accountRepository.findById(id).orElse(null);
        if(account == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    Map.of("Error", "Account not found")
            );
        }

        account.setStatus(request.getStatus());
        accountRepository.save(account);

        return ResponseEntity.status(HttpStatus.OK).body(
                new AccountStatusResponse(
                        account.getId(),
                        account.getStatus()
                )
        );
    }

    private String getAccountNumber(){
        int branchCode = 123456;
        return Integer.toString(branchCode) + (accountCount++).toString();
    }

}
