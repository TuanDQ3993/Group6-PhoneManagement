package com.example.PhoneManagement.repository;

import com.example.PhoneManagement.dto.request.PageDTO;
import com.example.PhoneManagement.entity.Users;
import com.example.PhoneManagement.entity.WarrantyRepair;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

public interface WarrantyRepairRepository extends JpaRepository<WarrantyRepair, Integer> {
    // In ra tat ca theo admin
    @Query("SELECT w FROM warrantyrepair w WHERE  w.isDeleted = false order by  w.repairDate desc  ")
    Page<WarrantyRepair> findAllBy(Pageable pageable);

    // In ra tat ca theo technical
    @Query("SELECT w FROM warrantyrepair w WHERE w.technical.userId = :technicalId and w.isDeleted = false order by  w.repairDate desc  ")
    Page<WarrantyRepair> findPaginated(@Param("technicalId") int technicalId, Pageable pageable);

    // In ra theo ngay
    @Query("SELECT w FROM warrantyrepair w WHERE w.technical.userId = :technicalId and w.isDeleted = false  AND w.repairDate = :repairDate order by w.repairDate desc")
    List<WarrantyRepair> findAllByTechnicalUserIdAndRepairDate(@Param("technicalId") int technicalId, @Param("repairDate") Date repairDate);

    boolean existsWarrantyRepairsByRepairDate(Date repairDate);


    // Dash board
    @Query("SELECT COUNT(w) FROM warrantyrepair w WHERE w.technical.userId = :technicalId AND w.isDeleted = false")
    long sumWarrantyRepairs(@Param("technicalId") int technicalId);

    @Query("SELECT COUNT(w) FROM warrantyrepair w WHERE w.technical.userId = :technicalId AND w.repairDate = :repairDate AND w.isDeleted = false")
    long sumWarrantyRepairsByRepairDate(@Param("technicalId") int technicalId, @Param("repairDate") Date repairDate);

    @Query("SELECT COUNT(w) FROM warrantyrepair w WHERE w.technical.userId = :technicalId AND w.status = 'Completed' AND w.repairDate = :repairDate AND w.isDeleted = false")
    long countWarrantyRepairsByRepairDateAndStatus(@Param("technicalId") int technicalId, @Param("repairDate") Date repairDate);

    @Query("SELECT COUNT(w) FROM warrantyrepair w WHERE w.technical.userId = :technicalId AND w.status = 'Completed' AND w.isDeleted = false")
    long countAllWarrantyRepairsAndStatus(@Param("technicalId") int technicalId);
    @Query("SELECT w.repairDate, COUNT(w) FROM warrantyrepair w WHERE w.isDeleted = false AND w.technical.userId = :technicalId GROUP BY w.repairDate ORDER BY w.repairDate")
    List<Object[]> findCountByRepairDate(@Param("technicalId") int technicalId);

    // Technical
    @Query("SELECT u FROM warrantyrepair u WHERE u.user.fullName LIKE %:query% OR u.productName  LIKE %:query% and u.isDeleted = false  and  u.technical.userId=:technicalId order by u.repairDate desc ")
    Page<WarrantyRepair> findWarrantyRepairByProductNameAndAndUser(@Param("query") String query, Pageable pageable, @Param("technicalId") int technicalId);

    @Query("SELECT w FROM warrantyrepair w WHERE  w.repairDate = :repairDate  and w.isDeleted = false  and   w.technical.userId=:technicalId order by  w.repairDate desc ")
    Page<WarrantyRepair> findAllByRepairDate(@Param("repairDate") Date repairDate, Pageable pageable, @Param("technicalId") int technicalId);

    @Query("SELECT w FROM warrantyrepair w WHERE  w.status = :status  and w.isDeleted = false and  w.technical.userId=:technicalId order by  w.repairDate desc ")
    Page<WarrantyRepair> findAllByStatus(@Param("status") String status, Pageable pageable, @Param("technicalId") int technicalId);

    @Query("SELECT u FROM warrantyrepair u WHERE u.user.fullName LIKE %:query% OR u.productName  LIKE %:query% and u.isDeleted = false   and u.status = :status and  u.repairDate = :repairDate  and  u.technical.userId=:technicalId  order by u.repairDate desc ")
    Page<WarrantyRepair> findAllByProductNameOrUserAndStatusAndRepairDateOrderByTechnical(@Param("query") String query, @Param("repairDate") Date date, @Param("status") String status, Pageable pageable, @Param("technicalId") int technicalId);

    // Admin
    @Query("SELECT u FROM warrantyrepair u WHERE u.user.fullName LIKE %:query% OR u.productName  LIKE %:query% and u.isDeleted = false   order by u.repairDate desc ")
    Page<WarrantyRepair> findAllByProductNameOrUser(@Param("query") String query, Pageable pageable);

    @Query("SELECT w FROM warrantyrepair w WHERE  w.repairDate = :repairDate and w.isDeleted = false  order by  w.repairDate desc ")
    Page<WarrantyRepair> findAllByRepairDate(@Param("repairDate") Date repairDate, Pageable pageable);

    @Query("SELECT w FROM warrantyrepair w WHERE  w.status = :status  and w.isDeleted = false   order by  w.repairDate desc ")
    Page<WarrantyRepair> findAllByStatus(@Param("status") String status, Pageable pageable);

    @Query("SELECT u FROM warrantyrepair u WHERE u.user.fullName LIKE %:query% OR u.productName  LIKE %:query%  and u.isDeleted = false  and u.status = :status and  u.repairDate = :repairDate  order by u.repairDate desc ")
    Page<WarrantyRepair> findAllByProductNameOrUserAndStatusAndRepairDate(@Param("query") String query, @Param("repairDate") Date date, @Param("status") String status, Pageable pageable);


    @Query("SELECT w FROM warrantyrepair w WHERE  w.repairDate = :repairDate and w.isDeleted = false  order by w.repairDate desc")
    List<WarrantyRepair> findAllByTechnicalUserIdAndRepairDate(@Param("repairDate") Date repairDate);


}
