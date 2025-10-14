package com.codewithdevil.bank.dtos;

import com.codewithdevil.bank.entities.AccountType;
import com.codewithdevil.bank.entities.CurrencyCode;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Currency;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AccountRequest {
    @NotBlank(message = "Account Type is required.")
    private AccountType type;

    @NotBlank(message = "Currency is required.")
    private CurrencyCode currency;
}
