package com.codewithdevil.bank.repositories;

import com.codewithdevil.bank.entities.Transfer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransferRepository extends JpaRepository<Transfer, Long> {
}
