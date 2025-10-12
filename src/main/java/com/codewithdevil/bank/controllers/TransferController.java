package com.codewithdevil.bank.controllers;

import com.codewithdevil.bank.dtos.TransferRequest;
import com.codewithdevil.bank.dtos.TransferResponse;
import com.codewithdevil.bank.entities.*;
import com.codewithdevil.bank.repositories.AccountRepository;
import com.codewithdevil.bank.repositories.TransactionRepository;
import com.codewithdevil.bank.repositories.TransferRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@RestController
@AllArgsConstructor
@RequestMapping("/transfers")
public class TransferController {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final TransferRepository transferRepository;
    private JwtController jwtController;

    @PostMapping
    public ResponseEntity<?> transferMoney(
            @RequestBody TransferRequest request,
            @RequestHeader("Authorization") String token,
            @RequestHeader("Idempotency-Key") String idempotencyKey
    ){

        var created = LocalDateTime.now();

        var jwt = jwtController.getJwt(token);
        if(jwt.isExpired()){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                    Map.of("Error", "Token is expired")
            );
        }

        var sourceAccount = accountRepository.findById(request.getSourceAccountId()).orElse(null);
        if(sourceAccount == null){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    Map.of("Error", "Source account not found")
            );
        }

        if(!String.valueOf(sourceAccount.getCustomer().getUser().getId()).equals(jwt.getUserId())){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                    Map.of("Error", "Unauthorized Customer")
            );
        }

        if(sourceAccount.getStatus() != AccountStatus.ACTIVE){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    Map.of("Error", "Source account status not active")
            );
        }

        if(sourceAccount.getCurrency() != request.getCurrency()){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    Map.of("Error", "Source account currency not match")
            );
        }

        if(sourceAccount.getBalance().subtract(request.getAmount()).intValue() > 0){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    Map.of("Error", "Source account balance not enough")
            );
        }

        var targetAccount = accountRepository.findById(request.getTargetAccountId()).orElse(null);
        if(targetAccount == null){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    Map.of("Error", "Target account not found")
            );
        }

        if(targetAccount.getStatus() != AccountStatus.ACTIVE){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    Map.of("Error", "Target account status not active")
            );
        }

        if(targetAccount.getCurrency() != request.getCurrency()){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    Map.of("Error", "Target account currency not match")
            );
        }

        var correlationId = UUID.randomUUID().toString();

        var transfer = Transfer.builder()
                .sourceAccount(sourceAccount)
                .targetAccount(targetAccount)
                .amount(request.getAmount())
                .currency(request.getCurrency())
                .idempotencyKey(idempotencyKey)
                .requestedByUserId(Long.parseLong(jwt.getUserId()))
                .correlationId(correlationId)
                .createdAt(created)
                .build();

        var transactionSource = Transaction.builder()
                .account(sourceAccount)
                .amount(request.getAmount())
                .balanceAfter(sourceAccount.getBalance().subtract(request.getAmount()))
                .correlationId(correlationId)
                .currency(request.getCurrency())
                .direction(TransactionDirection.DEBIT)
                .narrative(request.getNarrative())
                .createdAt(created)
                .build();

        var transactionTarget = Transaction.builder()
                .account(targetAccount)
                .amount(request.getAmount())
                .balanceAfter(targetAccount.getBalance().add(request.getAmount()))
                .correlationId(correlationId)
                .currency(request.getCurrency())
                .direction(TransactionDirection.CREDIT)
                .narrative(request.getNarrative())
                .createdAt(created)
                .build();

        sourceAccount.setBalance(sourceAccount.getBalance().subtract(request.getAmount()));
        targetAccount.setBalance(targetAccount.getBalance().add(request.getAmount()));

        accountRepository.save(sourceAccount);
        accountRepository.save(targetAccount);
        transactionRepository.save(transactionSource);
        transactionRepository.save(transactionTarget);

        transfer.setStatus(TransferStatus.COMPLETED);
        transfer.setCompletedAt(LocalDateTime.now());
        transferRepository.save(transfer);

        return ResponseEntity.status(HttpStatus.CREATED).body(
                new TransferResponse(
                        transfer.getId(),
                        transfer.getStatus(),
                        transfer.getCorrelationId(),
                        transactionSource.getId(),
                        transactionTarget.getId()
                )
        );

    }
}
