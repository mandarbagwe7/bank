package com.codewithdevil.bank.dtos;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AddressRequest {

    @NotBlank(message = "Address Line 1 is required.")
    @Column(name = "line1")
    private String line1;

    @Column(name = "line2")
    private String line2;

    @NotBlank(message = "City is required.")
    @Column(name = "city")
    private String city;

    @NotBlank(message = "State is required.")
    @Column(name = "state")
    private String state;

    @NotBlank(message = "Postal Code is required.")
    @Column(name = "postalCode")
    private String postalCode;

    @NotBlank(message = "Country is required.")
    @Column(name = "country")
    private String country;
}
