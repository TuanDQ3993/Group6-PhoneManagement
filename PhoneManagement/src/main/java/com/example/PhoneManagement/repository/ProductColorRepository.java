package com.example.PhoneManagement.repository;

import com.example.PhoneManagement.entity.ProductInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductColorRepository extends JpaRepository<ProductInfo, Integer> {

}
