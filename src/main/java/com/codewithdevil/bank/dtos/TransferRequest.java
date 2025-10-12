package com.codewithdevil.bank.dtos;

import com.codewithdevil.bank.entities.CurrencyCode;
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
    private Long sourceAccountId;
    private Long targetAccountId;
    private BigDecimal amount;
    private CurrencyCode currency;
    private String narrative;
}
