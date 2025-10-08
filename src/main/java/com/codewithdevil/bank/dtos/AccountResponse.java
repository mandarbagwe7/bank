package com.codewithdevil.bank.dtos;

import com.codewithdevil.bank.entities.AccountStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AccountResponse {
    private Long accountId;
    private AccountStatus accountStatus; // Need to put a check here
    private BigDecimal balance;
}
