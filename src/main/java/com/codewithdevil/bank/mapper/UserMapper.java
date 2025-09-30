package com.codewithdevil.bank.mapper;

import com.codewithdevil.bank.dtos.RegisterResponse;
import com.codewithdevil.bank.entities.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mapping(source = "id", target = "userId")
    RegisterResponse toRegisterResponse(User user);
}
