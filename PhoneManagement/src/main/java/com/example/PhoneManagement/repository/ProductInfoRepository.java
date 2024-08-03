package com.example.PhoneManagement.repository;

import com.example.PhoneManagement.entity.ProductInfo;
import com.example.PhoneManagement.entity.Products;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProductInfoRepository extends JpaRepository<ProductInfo, Integer> {
    @Query("SELECT p FROM Products p WHERE p.category.categoryId = :categoryId ORDER BY p.createdAt DESC")
    Page<Products> findTop4ByCategoryIdOrderByCreatedAtDesc(@Param("categoryId") int categoryId, Pageable pageable);
}
