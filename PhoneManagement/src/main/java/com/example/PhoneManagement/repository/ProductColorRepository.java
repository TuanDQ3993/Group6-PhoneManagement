package com.example.PhoneManagement.repository;

import com.example.PhoneManagement.entity.ProductInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductColorRepository extends JpaRepository<ProductInfo, Integer> {
    @Query("SELECT p.quantity FROM productinfo p Where p.productcolorId= :productcolorId")
    int getQuantityProduct(@Param("productcolorId") int productcolorId);
}
