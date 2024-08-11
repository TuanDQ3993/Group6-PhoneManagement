package com.example.PhoneManagement.repository;

import com.example.PhoneManagement.entity.WarrantyRepair;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

public interface WarrantyRepairRepository extends JpaRepository<WarrantyRepair, Integer> {
    // In ra tất cả theo admin
    @Query("SELECT w FROM warrantyrepair w WHERE w.isDeleted = false ORDER BY w.repairDate DESC")
    Page<WarrantyRepair> findAllBy(Pageable pageable);

    // In ra tất cả theo technical
    @Query("SELECT w FROM warrantyrepair w WHERE w.technical.userId = :technicalId AND w.isDeleted = false ORDER BY w.repairDate DESC")
    Page<WarrantyRepair> findPaginated(@Param("technicalId") int technicalId, Pageable pageable);

    // In ra theo ngày
    @Query("SELECT w FROM warrantyrepair w WHERE w.technical.userId = :technicalId AND w.isDeleted = false AND Date(w.repairDate) = :repairDate ORDER BY w.repairDate DESC")
    List<WarrantyRepair> findAllByTechnicalUserIdAndRepairDate(@Param("technicalId") int technicalId, @Param("repairDate") Date repairDate);

    @Query("SELECT CASE WHEN COUNT(w) > 0 THEN true ELSE false END FROM warrantyrepair w WHERE DATE(w.repairDate) = :repairDate AND w.isDeleted = false")
    boolean existsWarrantyRepairsByRepairDate(@Param("repairDate") Date repairDate);


    // Dash board technical
    @Query("SELECT COUNT(w) FROM warrantyrepair w WHERE w.technical.userId = :technicalId AND w.isDeleted = false")
    long sumWarrantyRepairs(@Param("technicalId") int technicalId);

    @Query("SELECT COUNT(w) FROM warrantyrepair w WHERE w.technical.userId = :technicalId AND Date(w.repairDate) = :repairDate AND w.isDeleted = false")
    long sumWarrantyRepairsByRepairDate(@Param("technicalId") int technicalId, @Param("repairDate") Date repairDate);

    @Query("SELECT COUNT(w) FROM warrantyrepair w WHERE w.technical.userId = :technicalId AND w.status = 'Warranty Completed' AND Date(w.repairDate) = :repairDate AND w.isDeleted = false")
    long countWarrantyRepairsByRepairDateAndStatus(@Param("technicalId") int technicalId, @Param("repairDate") Date repairDate);

    @Query("SELECT COUNT(w) FROM warrantyrepair w WHERE w.technical.userId = :technicalId AND w.status = 'Warranty Completed' AND w.isDeleted = false")
    long countAllWarrantyRepairsAndStatus(@Param("technicalId") int technicalId);

    @Query("SELECT w.repairDate, COUNT(w) FROM warrantyrepair w WHERE w.isDeleted = false AND w.technical.userId = :technicalId GROUP BY Date(w.repairDate) ORDER BY w.repairDate DESC")
    List<Object[]> findCountByRepairDate(@Param("technicalId") int technicalId);

    // Dashboard admin
    @Query("SELECT COUNT(w) FROM warrantyrepair w WHERE w.isDeleted = false")
    long countAllBy();

    @Query("SELECT COUNT(w) FROM warrantyrepair w WHERE w.status = 'Warranty Completed' AND w.isDeleted = false")
    long countWarrantyRepairBy();

    // Technical
    @Query("SELECT w FROM warrantyrepair w WHERE " +
            "(w.user.fullName LIKE %:query% OR w.productName LIKE %:query%) " +
            "AND w.isDeleted = false " +
            "AND (:status IS NULL OR w.status = :status) " +
            "AND (:repairDate IS NULL OR DATE(w.repairDate) = :repairDate) " +
            "AND w.technical.userId = :technicalId " +
            "ORDER BY w.repairDate DESC")
    Page<WarrantyRepair> findWarrantyRepairByProductNameAndAndUser(@Param("query") String query, Pageable pageable, @Param("technicalId") int technicalId);

    @Query("SELECT w FROM warrantyrepair w WHERE Date(w.repairDate) = :repairDate AND w.isDeleted = false AND w.technical.userId = :technicalId ORDER BY w.warrantyId DESC")
    Page<WarrantyRepair> findAllByRepairDate(@Param("repairDate") Date repairDate, Pageable pageable, @Param("technicalId") int technicalId);

    @Query("SELECT w FROM warrantyrepair w WHERE w.status = :status AND w.isDeleted = false AND w.technical.userId = :technicalId ORDER BY w.warrantyId DESC")
    Page<WarrantyRepair> findAllByStatus(@Param("status") String status, Pageable pageable, @Param("technicalId") int technicalId);

    @Query("SELECT w FROM warrantyrepair w WHERE (w.user.fullName LIKE %:query% OR w.productName LIKE %:query%) AND w.isDeleted = false AND w.status = :status AND Date(w.repairDate) = :repairDate AND w.technical.userId = :technicalId ORDER BY w.repairDate DESC")
    Page<WarrantyRepair> findAllByProductNameOrUserAndStatusAndRepairDateOrderByTechnical(@Param("query") String query, @Param("repairDate") Date date, @Param("status") String status, Pageable pageable, @Param("technicalId") int technicalId);

    @Query("SELECT w FROM warrantyrepair w WHERE w.productName LIKE %:productName% AND w.status = :status AND FUNCTION('DATE', w.repairDate) = :repairDate AND w.technical.userId = :technicalId AND w.isDeleted = false ORDER BY w.repairDate DESC")
    Page<WarrantyRepair> findAllByProductNameAndStatusAndDateByTechnical(@Param("productName") String productName, @Param("status") String status, @Param("repairDate") Date repairDate, @Param("technicalId") int technicalId, Pageable pageable);

    @Query("SELECT w FROM warrantyrepair w WHERE w.productName LIKE %:productName% AND FUNCTION('DATE', w.repairDate) = :repairDate AND w.technical.userId = :technicalId AND w.isDeleted = false ORDER BY w.repairDate DESC")
    Page<WarrantyRepair> findAllByProductNameAndRepairDateByTechnical(@Param("productName") String productName, @Param("repairDate") Date repairDate, @Param("technicalId") int technicalId, Pageable pageable);

    @Query("SELECT w FROM warrantyrepair w WHERE w.status = :status AND FUNCTION('DATE', w.repairDate) = :repairDate AND w.technical.userId = :technicalId AND w.isDeleted = false ORDER BY w.repairDate DESC")
    Page<WarrantyRepair> findAllByStatusAndRepairDateByTechnical(@Param("status") String status, @Param("repairDate") Date repairDate, @Param("technicalId") int technicalId, Pageable pageable);

    @Query("SELECT w FROM warrantyrepair w WHERE w.productName LIKE %:productName% AND w.status = :status AND w.technical.userId = :technicalId AND w.isDeleted = false ORDER BY w.repairDate DESC")
    Page<WarrantyRepair> findAllByProductNameAndStatusByTechnical(@Param("productName") String productName, @Param("status") String status, @Param("technicalId") int technicalId, Pageable pageable);

    // Admin
    @Query("SELECT w FROM warrantyrepair w WHERE (w.user.fullName LIKE %:query% OR w.productName LIKE %:query%) AND w.isDeleted = false ORDER BY w.repairDate DESC")
    Page<WarrantyRepair> findAllByProductNameOrUser(@Param("query") String query, Pageable pageable);

    @Query("SELECT w FROM warrantyrepair w WHERE Date(w.repairDate) = :repairDate AND w.isDeleted = false ORDER BY w.warrantyId DESC")
    Page<WarrantyRepair> findAllByRepairDate(@Param("repairDate") Date repairDate, Pageable pageable);

    @Query("SELECT w FROM warrantyrepair w WHERE w.status = :status AND w.isDeleted = false ORDER BY w.warrantyId DESC")
    Page<WarrantyRepair> findAllByStatus(@Param("status") String status, Pageable pageable);

    @Query("SELECT w FROM warrantyrepair w WHERE " +
            "(w.user.fullName LIKE %:query% OR w.productName LIKE %:query%) " +
            "AND w.isDeleted = false " +
            "AND (:status IS NULL OR w.status = :status) " +
            "AND (:repairDate IS NULL OR DATE(w.repairDate) = :repairDate) " +
            "ORDER BY w.repairDate DESC")
    Page<WarrantyRepair> findAllByProductNameOrUserAndStatusAndRepairDate(@Param("query") String query, @Param("repairDate") Date date, @Param("status") String status, Pageable pageable);

    @Query("SELECT w FROM warrantyrepair w WHERE Date(w.repairDate) = :repairDate AND w.isDeleted = false ORDER BY w.repairDate DESC")
    List<WarrantyRepair> findAllByTechnicalUserIdAndRepairDate(@Param("repairDate") Date repairDate);

    @Query(value = "SELECT technical_id FROM (SELECT s.user_id AS technical_id, COUNT(o.warranty_id) AS order_count FROM phonemanagement.useraccount s LEFT JOIN phonemanagement.warrantyrepair o ON s.user_id = o.technical_id AND DATE(o.repair_date) = CURDATE() WHERE s.role_id = 3 AND s.active = 1 GROUP BY s.user_id) AS order_counts ORDER BY order_count ASC LIMIT 1", nativeQuery = true)
    int getTechnicalMinOrder();

    @Query("SELECT w FROM warrantyrepair w WHERE w.order.orderId = :orderId")
    List<WarrantyRepair> findByOrderAndProduct(@Param("orderId") int orderId);

    @Query("SELECT w FROM warrantyrepair w WHERE w.productName LIKE %:productName% AND FUNCTION('DATE', w.repairDate) = :repairDate AND w.isDeleted = false ORDER BY w.repairDate DESC")
    Page<WarrantyRepair> findAllByProductNameAndRepairDateByAdmin(@Param("productName") String productName, @Param("repairDate") Date repairDate, Pageable pageable);

    @Query("SELECT w FROM warrantyrepair w WHERE w.status = :status AND FUNCTION('DATE', w.repairDate) = :repairDate AND w.isDeleted = false ORDER BY w.repairDate DESC")
    Page<WarrantyRepair> findAllByStatusAndRepairDateByAdmin(@Param("status") String status, @Param("repairDate") Date repairDate, Pageable pageable);

    @Query("SELECT w FROM warrantyrepair w WHERE w.productName LIKE %:productName% AND w.status = :status AND w.isDeleted = false ORDER BY w.repairDate DESC")
    Page<WarrantyRepair> findAllByProductNameAndStatusByAdmin(@Param("productName") String productName, @Param("status") String status, Pageable pageable);

}
