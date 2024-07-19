package com.example.PhoneManagement.repository;

import com.example.PhoneManagement.entity.Roles;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<Roles, Integer> {
    public Roles findByRoleName(String roleName);
}
