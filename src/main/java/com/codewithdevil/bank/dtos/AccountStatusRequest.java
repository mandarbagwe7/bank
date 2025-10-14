package com.codewithdevil.bank.dtos;

import com.codewithdevil.bank.entities.AccountStatus;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AccountStatusRequest {
    @NotBlank(message = "Account Status is required.")
    private AccountStatus status;
}
