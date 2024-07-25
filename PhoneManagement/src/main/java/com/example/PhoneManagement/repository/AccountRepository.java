package com.example.PhoneManagement.repository;

import com.example.PhoneManagement.entity.Users;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AccountRepository extends JpaRepository<Users, Integer> {
    // Search user in account list by fullName
    @Query("SELECT u FROM useraccount u WHERE LOWER(u.fullName) LIKE LOWER(CONCAT('%', :text, '%'))")
    List<Users> searchUsersRepo(@Param("text") String text);

    // Ban user
    @Modifying
    @Transactional
    @Query("UPDATE useraccount u SET u.active = true WHERE u.userId = :userId")
    void unBanUser(@Param("userId") int userId);

    // Unban user in
    @Modifying
    @Transactional
    @Query("UPDATE useraccount u SET u.active = false WHERE u.userId = :userId")
    void banUser(@Param("userId") int userId);

    // Show all user in account list
    @Query("SELECT u FROM useraccount u ORDER BY u.createdAt")
    List<Users> findAllOrderByCreatedAt();

    // Change role of account
    @Modifying
    @Transactional
    @Query("UPDATE useraccount u SET u.role.roleId = :role WHERE u.userId = :userId")
    void changeRole(@Param("userId") int userId, @Param("role") int role );

}
