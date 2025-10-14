package com.codewithdevil.bank.dtos;

import com.codewithdevil.bank.entities.CurrencyCode;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TransferRequest {
    @NotBlank(message = "Source Account is required.")
    private Long sourceAccountId;

    @NotBlank(message = "Target Account is required.")
    private Long targetAccountId;

    @NotBlank(message = "Amount is required.")
    @Positive(message = "Amount should be greater than 0.")
    private BigDecimal amount;

    @NotBlank(message = "Currency is required.")
    private CurrencyCode currency;

    private String narrative;
}
