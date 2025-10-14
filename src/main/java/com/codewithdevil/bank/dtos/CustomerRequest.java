package com.codewithdevil.bank.dtos;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CustomerRequest {

    @NotBlank(message = "Date of Birth is required.")
    @Column(name = "dob")
    private LocalDate dob;

    @NotBlank(message = "Phone is required.")
    @Column(name = "phone")
    private String phone;

    @NotBlank(message = "Address is required.")
    @Column(name = "address")
    private AddressRequest address;
}
