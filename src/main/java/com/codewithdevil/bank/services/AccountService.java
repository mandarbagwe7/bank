package com.codewithdevil.bank.services;

import com.codewithdevil.bank.controllers.JwtController;
import com.codewithdevil.bank.dtos.AccountRequest;
import com.codewithdevil.bank.dtos.AccountResponse;
import com.codewithdevil.bank.dtos.AccountStatusRequest;
import com.codewithdevil.bank.dtos.AccountStatusResponse;
import com.codewithdevil.bank.entities.Account;
import com.codewithdevil.bank.entities.AccountStatus;
import com.codewithdevil.bank.entities.KycStatus;
import com.codewithdevil.bank.exceptions.AccountNotFoundException;
import com.codewithdevil.bank.exceptions.CustomerKYCInvalidException;
import com.codewithdevil.bank.exceptions.CustomerNotFoundException;
import com.codewithdevil.bank.exceptions.JwtInvalidException;
import com.codewithdevil.bank.repositories.AccountRepository;
import com.codewithdevil.bank.repositories.CustomerRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@AllArgsConstructor
public class AccountService {

    private JwtController jwtController;
    private CustomerRepository customerRepository;
    private AccountRepository accountRepository;
    private static Long accountCount = 0L;

    public AccountResponse createAccount(AccountRequest request, String token) {
        var jwt = jwtController.getJwt(token);
        if(jwt.isExpired()){
            throw new JwtInvalidException();
        }

        var customer = customerRepository.findByUserId(Long.parseLong(jwt.getUserId())).orElse(null);
        if(customer == null) {
            throw new CustomerNotFoundException();
        }

        if(!customer.getKycStatus().equals(KycStatus.ACTIVE)){
            throw new CustomerKYCInvalidException();
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

        return new AccountResponse(
                        account.getId(),
                        account.getStatus(),
                        account.getBalance()
                );
    }

    public AccountStatusResponse updateAccountStatus(
            Long id,
            String token,
            AccountStatusRequest request
    ){
        var jwt = jwtController.getJwt(token);
        if(jwt.isExpired()){
            throw new JwtInvalidException();
        }

        var account = accountRepository.findById(id).orElse(null);
        if(account == null) {
            throw new AccountNotFoundException();
        }

        account.setStatus(request.getStatus());
        accountRepository.save(account);

        return new AccountStatusResponse(
                        account.getId(),
                        account.getStatus()
        );
    }

    private String getAccountNumber(){
        int branchCode = 123456;
        return Integer.toString(branchCode) + (accountCount++).toString();
    }
}
