package com.example.PhoneManagement.repository;

import com.example.PhoneManagement.dto.request.PageDTO;
import com.example.PhoneManagement.entity.WarrantyRepair;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

public interface WarrantyRepairRepository  extends JpaRepository<WarrantyRepair, Integer> {

    Page<WarrantyRepair> findAllBy(Pageable pageable);

    @Query("SELECT w FROM warrantyrepair w WHERE w.technical.userId = :technicalId")
    Page<WarrantyRepair> findPaginated(@Param("technicalId") int technicalId, Pageable pageable);

    @Query("SELECT w FROM warrantyrepair w WHERE w.technical.userId = :technicalId AND w.repairDate = :repairDate")
    List<WarrantyRepair> findAllByTechnicalUserIdAndRepairDate(@Param("technicalId") int technicalId, @Param("repairDate") Date repairDate);

    boolean existsWarrantyRepairsByRepairDate(Date repairDate);

}
