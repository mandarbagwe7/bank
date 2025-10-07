package com.codewithdevil.bank.dtos;

import jakarta.persistence.Column;
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
    @Column(name = "fullName")
    private String fullName;

    @Column(name = "dob")
    private LocalDate dob;

    @Column(name = "phone")
    private String phone;

    @Column(name = "address")
    private AddressRequest address;
}
