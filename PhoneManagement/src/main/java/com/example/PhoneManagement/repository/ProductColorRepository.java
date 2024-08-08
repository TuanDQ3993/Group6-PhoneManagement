
package com.example.PhoneManagement.repository;

import com.example.PhoneManagement.entity.ProductInfo;
import com.example.PhoneManagement.entity.Products;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductColorRepository extends JpaRepository<ProductInfo, Integer> {
    @Query("SELECT p FROM Products p WHERE p.category.categoryId = :categoryId ORDER BY p.createdAt DESC")
    Page<Products> findTop4ByCategoryIdOrderByCreatedAtDesc(@Param("categoryId") int categoryId, Pageable pageable);

    @Query("SELECT p.quantity FROM productinfo p Where p.productcolorId= :productcolorId")
    int getQuantityProduct(@Param("productcolorId") int productcolorId);

    @Query("select count (n) from productinfo n where n.isDeleted = true")
    long countAllBy();
}
