package com.codewithdevil.bank.services;

import com.codewithdevil.bank.controllers.JwtController;
import com.codewithdevil.bank.dtos.TransferRequest;
import com.codewithdevil.bank.dtos.TransferResponse;
import com.codewithdevil.bank.entities.*;
import com.codewithdevil.bank.exceptions.*;
import com.codewithdevil.bank.repositories.AccountRepository;
import com.codewithdevil.bank.repositories.TransactionRepository;
import com.codewithdevil.bank.repositories.TransferRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@AllArgsConstructor
public class TransferService {
    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final TransferRepository transferRepository;
    private JwtController jwtController;

    public TransferResponse transferMoney(
            TransferRequest request,
            String token,
            String idempotencyKey
    ){

        var created = LocalDateTime.now();

        var jwt = jwtController.getJwt(token);
        if(jwt.isExpired()){
            throw new JwtInvalidException();
        }

        var sourceAccount = accountRepository.findById(request.getSourceAccountId()).orElse(null);
        if(sourceAccount == null){
            throw new AccountNotFoundException("Source account not found");
        }

        if(!String.valueOf(sourceAccount.getCustomer().getUser().getId()).equals(jwt.getUserId())){
            throw new UnauthorizeCustomerException();
        }

        if(sourceAccount.getStatus() != AccountStatus.ACTIVE){
            throw new AccountStatusInactiveException("Source account status not active");
        }

        if(sourceAccount.getCurrency() != request.getCurrency()){
            throw new CurrencyMismatchException("Source account currency not match");
        }

        if(sourceAccount.getBalance().subtract(request.getAmount()).intValue() < 0){
            throw new LowBalanceException();
        }

        var targetAccount = accountRepository.findById(request.getTargetAccountId()).orElse(null);
        if(targetAccount == null){
            throw new AccountNotFoundException("Target account not found");
        }

        if(targetAccount.getStatus() != AccountStatus.ACTIVE){
            throw new AccountStatusInactiveException("Target account status not active");
        }

        if(targetAccount.getCurrency() != request.getCurrency()){
            throw new CurrencyMismatchException("Target account currency not match");
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

        return new TransferResponse(
            transfer.getId(),
            transfer.getStatus(),
            transfer.getCorrelationId(),
            transactionSource.getId(),
            transactionTarget.getId()
        );

    }
}
