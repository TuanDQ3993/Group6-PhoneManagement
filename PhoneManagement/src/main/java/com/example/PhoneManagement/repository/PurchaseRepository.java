package com.example.PhoneManagement.repository;

import com.example.PhoneManagement.entity.Purchase;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface PurchaseRepository extends JpaRepository<Purchase,Integer> {
    @Query("SELECT p FROM Purchase p WHERE p.user.userId = :userId")
    Page<Purchase> findByUserId(@Param("userId") int userId, Pageable pageable);

    @Query("SELECT Date(p.purchaseDate), SUM(p2.quantity*p2.price) FROM Purchase p join purchasedetail p2 on p.purchaseId=p2.purchase.purchaseId where p.user.userId= :userId group by Date(p.purchaseDate)")
    List<Object[]> findDailyPurchasesForUser(@Param("userId") int userId);

    @Query("SELECT DATE(p.purchaseDate) as date, SUM(p.totalAmount) as totalAmount " +
            "FROM Purchase p WHERE p.user.userId = :userId " +
            "AND (:startDate IS NULL OR DATE(p.purchaseDate) >= :startDate) " +
            "AND (:endDate IS NULL OR DATE(p.purchaseDate) <= :endDate) " +
            "AND (:status IS NULL OR :status = '' OR p.status = :status) " +
            "GROUP BY DATE(p.purchaseDate)")
    List<Object[]> findFilteredDailyPurchasesForUser(@Param("userId") int userId,
                                                     @Param("startDate") LocalDate startDate,
                                                     @Param("endDate") LocalDate endDate,
                                                     @Param("status") String status);
}
