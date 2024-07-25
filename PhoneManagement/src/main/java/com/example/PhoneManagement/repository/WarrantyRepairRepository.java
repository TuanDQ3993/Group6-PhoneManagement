package com.example.PhoneManagement.repository;

import com.example.PhoneManagement.entity.WarrantyRepair;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface WarrantyRepairRepository  extends JpaRepository<WarrantyRepair, Integer> {

    // Get warranty list by id technical staff
    @Query("select w from warrantyrepair  w where w.technicalId =: technicalId order by w.repairDate")
    List<WarrantyRepair> findAllById(@Param("technicalId") int technicalId);

    // Search by name
    @Query("select w from  warrantyrepair  w where lower(w.user.fullName) like  lower((concat('%', :text, '%')))")
    List<WarrantyRepair> searchWarrantyRepairByName(@Param("text") String text);
}
