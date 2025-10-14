package com.codewithdevil.bank.services;

import com.codewithdevil.bank.controllers.JwtController;
import com.codewithdevil.bank.dtos.CustomerRequest;
import com.codewithdevil.bank.dtos.CustomerResponse;
import com.codewithdevil.bank.dtos.KycStatusRequest;
import com.codewithdevil.bank.entities.KycStatus;
import com.codewithdevil.bank.exceptions.CustomerConflictException;
import com.codewithdevil.bank.exceptions.CustomerNotFoundException;
import com.codewithdevil.bank.exceptions.JwtInvalidException;
import com.codewithdevil.bank.exceptions.UserNotFoundException;
import com.codewithdevil.bank.mapper.CustomerMapper;
import com.codewithdevil.bank.repositories.CustomerRepository;
import com.codewithdevil.bank.repositories.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;

@Service
@AllArgsConstructor
public class CustomerService {

    private final JwtController jwtController;
    private final UserRepository userRepository;
    private final CustomerMapper customerMapper;
    private final CustomerRepository customerRepository;

    public CustomerResponse createCustomer(
            CustomerRequest request,
            String token
    ){
        token = token.replace("Bearer ", "");
        var jwt = jwtController.getJwt(token);

        if(jwt.isExpired()){
            throw new JwtInvalidException();
        }

        var user = userRepository.findById(Long.parseLong(jwt.getUserId())).orElse(null);
        if(user == null) {
            throw new UserNotFoundException();
        }

        var customer = customerRepository.findByUserId(user.getId()).orElse(null);
        if(customer != null) {
            throw new CustomerConflictException();
        }

        var customerNew = customerMapper.toCustomer(request);
        customerNew.setFullName(user.getFullName());
        customerNew.setUser(user);
        customerNew.setCreatedAt(LocalDateTime.now());
        customerNew.setUpdatedAt(LocalDateTime.now());
        customerNew.setEmailCopy(user.getEmail());
        customerNew.setKycStatus(KycStatus.PENDING);

        customerRepository.save(customerNew);

        return new CustomerResponse(
                customerNew.getId(),
                customerNew.getKycStatus()
        );

    }

    public CustomerResponse getSelf(
            String token
    ){
        token = token.replace("Bearer ", "");
        var jwt = jwtController.getJwt(token);

        if(jwt.isExpired()){
            throw new JwtInvalidException();
        }

        var user = userRepository.findById(Long.parseLong(jwt.getUserId())).orElse(null);
        if(user == null) {
            throw new UserNotFoundException();
        }

        var customer = customerRepository.findByUserId(user.getId()).orElse(null);
        if(customer == null) {
            throw new CustomerNotFoundException();
        }

        return new CustomerResponse(
                customer.getId(),
                customer.getKycStatus()
        );
    }

    public ResponseEntity<?> updateCustomer(
            long id,
            KycStatusRequest request,
            String token
    ){
        token = token.replace("Bearer ", "");
        var jwt = jwtController.getJwt(token);
        if(jwt.isExpired()){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                    Map.of("Error", "Token is expired")
            );
        }

        var customer = customerRepository.findById(id).orElse(null);
        if(customer == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        if(customer.getKycStatus() != request.getStatus()) {
            customer.setKycStatus(request.getStatus());
            customer.setUpdatedAt(LocalDateTime.now());
            customerRepository.save(customer);
        }

        return ResponseEntity.status(HttpStatus.OK).body(
                new CustomerResponse(
                        customer.getId(),
                        customer.getKycStatus()
                )
        );
    }
}
