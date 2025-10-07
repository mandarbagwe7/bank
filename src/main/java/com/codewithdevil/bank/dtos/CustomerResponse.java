package com.codewithdevil.bank.dtos;

import com.codewithdevil.bank.entities.KycStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CustomerResponse {
    private Long customerId;
    private KycStatus kycStatus;
}
