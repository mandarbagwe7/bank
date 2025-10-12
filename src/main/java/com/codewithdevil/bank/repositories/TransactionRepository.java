package com.codewithdevil.bank.repositories;

import com.codewithdevil.bank.entities.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository extends JpaRepository<Transaction, Integer> {
}
