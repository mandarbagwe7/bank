package com.codewithdevil.bank.repositories;

import com.codewithdevil.bank.entities.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {
}
