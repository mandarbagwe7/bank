package com.codewithdevil.bank.mapper;

import com.codewithdevil.bank.dtos.CustomerRequest;
import com.codewithdevil.bank.entities.Customer;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CustomerMapper{
    @Mapping(source = "address.line1", target = "addressLine1")
    @Mapping(source = "address.line2", target = "addressLine2")
    @Mapping(source = "address.city", target = "city")
    @Mapping(source = "address.state", target = "state")
    @Mapping(source = "address.postalCode", target = "postalCode")
    @Mapping(source = "address.country", target = "country")
    @Mapping(target = "id", ignore = true)
    Customer toCustomer(CustomerRequest request);
}
