package com.codewithdevil.bank.controllers;

import com.codewithdevil.bank.dtos.CustomerRequest;
import com.codewithdevil.bank.dtos.CustomerResponse;
import com.codewithdevil.bank.dtos.KycStatusRequest;
import com.codewithdevil.bank.entities.KycStatus;
import com.codewithdevil.bank.mapper.CustomerMapper;
import com.codewithdevil.bank.repositories.CustomerRepository;
import com.codewithdevil.bank.repositories.UserRepository;
import com.codewithdevil.bank.services.Jwt;
import com.codewithdevil.bank.services.JwtService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@AllArgsConstructor
@RequestMapping("/customers")
public class CustomerController {

    private final JwtController jwtController;
    private final UserRepository userRepository;
    private final CustomerMapper customerMapper;
    private final CustomerRepository customerRepository;

    @PostMapping
    public ResponseEntity<?> createCustomer(
            @RequestBody CustomerRequest request,
            @RequestHeader(name = "Authorization") String token
    ){
        var jwt = jwtController.getJwt(token);

        if(jwt.isExpired()){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                    Map.of("Error", "Token is expired")
            );
        }

        var user = userRepository.findById(Long.parseLong(jwt.getUserId())).orElse(null);
        if(user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        var customer = customerRepository.findByUserId(user.getId()).orElse(null);
        if(customer != null) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(
                    Map.of("Error", "Customer already exists")
            );
        }

        var customerNew = customerMapper.toCustomer(request);
        customerNew.setFullName(user.getFullName());
        customerNew.setUser(user);
        customerNew.setCreatedAt(LocalDateTime.now());
        customerNew.setUpdatedAt(LocalDateTime.now());
        customerNew.setEmailCopy(user.getEmail());
        customerNew.setKycStatus(KycStatus.PENDING);

        customerRepository.save(customerNew);

        return ResponseEntity.status(HttpStatus.CREATED).body(
                new CustomerResponse(
                        customerNew.getId(),
                        customerNew.getKycStatus()
                )
        );

    }

    @GetMapping("/me")
    public ResponseEntity<?> getSelf(
            @RequestHeader(name = "Authorization") String token
    ){
        var jwt = jwtController.getJwt(token);

        if(jwt.isExpired()){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                    Map.of("Error", "Token is expired")
            );
        }

        var user = userRepository.findById(Long.parseLong(jwt.getUserId())).orElse(null);
        if(user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        var customer = customerRepository.findByUserId(user.getId()).orElse(null);
        if(customer == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        return ResponseEntity.status(HttpStatus.OK).body(
                new CustomerResponse(
                        customer.getId(),
                        customer.getKycStatus()
                )
        );
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<?> updateCustomer(
            @PathVariable long id,
            @RequestBody KycStatusRequest request,
            @RequestHeader(name = "Authorization") String token
    ){
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
