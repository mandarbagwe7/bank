package com.codewithdevil.bank.repositories;

import com.codewithdevil.bank.entities.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
    Optional<Customer> findByUserId(long userId);
}
