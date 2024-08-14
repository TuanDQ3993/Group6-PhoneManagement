package com.example.PhoneManagement.repository;

import com.example.PhoneManagement.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<Users, Integer> {
    Optional<Users> findByUserName(String UserName);

    Boolean existsByUserName(String UserName);

    @Query(value = "SELECT user_id FROM useraccount WHERE username = :userName",nativeQuery = true)
    int findIdByUserName(@Param("userName") String UserName);

    @Query(value = "SELECT ua FROM useraccount ua WHERE ua.role.roleId= 2 and ua.active= true")
    List<Users> getAllSale();


}