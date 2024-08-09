package com.example.PhoneManagement.repository;

import com.example.PhoneManagement.entity.Users;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Pageable;
import java.util.List;

@Repository
public interface AccountRepository extends JpaRepository<Users, Integer> {
    @Query("SELECT u FROM useraccount u WHERE (u.fullName LIKE %:query%) AND u.role.roleId = :role order by u.createdAt desc ")
    Page<Users> findByRoleAndFullName(String query, int role, Pageable pageable);

    @Query("SELECT u FROM useraccount u WHERE u.userName LIKE %:query% OR u.userName LIKE %:query% order by u.createdAt desc ")
    Page<Users> findByFullName(String query, Pageable pageable);

    @Query("SELECT u FROM useraccount u WHERE u.role.roleId = :role order by u.createdAt desc ")
    Page<Users> findByRole(int role, Pageable pageable);
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
    @Query("SELECT u FROM useraccount u ORDER BY u.createdAt desc ")
    List<Users> findAllOrderByCreatedAt();

    // Change role of account
    @Modifying
    @Transactional
    @Query("UPDATE useraccount u SET u.role.roleId = :role WHERE u.userId = :userId")
    void changeRole(@Param("userId") int userId, @Param("role") int role );

    boolean existsByPhoneNumber(String phoneNumber);
    boolean existsByUserName(String email);
}
