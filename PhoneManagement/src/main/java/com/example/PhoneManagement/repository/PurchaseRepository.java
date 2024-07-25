package com.example.PhoneManagement.repository;

import com.example.PhoneManagement.entity.Purchase;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PurchaseRepository extends JpaRepository<Purchase,Integer> {
    @Query("SELECT p FROM Purchase p WHERE p.user.userId = :userId")
    Page<Purchase> findByUserId(@Param("userId") int userId, Pageable pageable);
}
