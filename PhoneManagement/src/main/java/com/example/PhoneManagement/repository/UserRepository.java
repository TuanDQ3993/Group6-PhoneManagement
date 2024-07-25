package com.example.PhoneManagement.repository;

import com.example.PhoneManagement.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<Users, Integer> {
    Optional<Users>  findByUserName(String UserName);
    Boolean existsByUserName(String UserName);
    int findIdByUserName(String UserName);
}
