package com.codewithdevil.bank.dtos;

import com.codewithdevil.bank.entities.AccountStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AccountStatusRequest {
    private AccountStatus status;
}
