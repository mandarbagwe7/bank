package com.codewithdevil.bank.dtos;

import com.codewithdevil.bank.entities.KycStatus;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class KycStatusRequest {
    @NotBlank(message = "Status is required.")
    private KycStatus status;
}
