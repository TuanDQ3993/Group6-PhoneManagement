package com.example.PhoneManagement.repository;

import com.example.PhoneManagement.entity.PurchaseDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PurchaseDetailsRepository extends JpaRepository<PurchaseDetail,Integer> {
}
