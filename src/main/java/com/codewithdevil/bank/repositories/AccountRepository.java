package com.codewithdevil.bank.repositories;

import com.codewithdevil.bank.entities.Account;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<Account,Long> {

}
