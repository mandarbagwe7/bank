package com.codewithdevil.bank.dtos;

import com.codewithdevil.bank.entities.CurrencyCode;
import com.codewithdevil.bank.entities.TransferStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TransferResponse {
    private Long transferId;
    private TransferStatus status;
    private String correlationId;
    private Long debitTransactionId;
    private Long creditTransactionAmount;
}
