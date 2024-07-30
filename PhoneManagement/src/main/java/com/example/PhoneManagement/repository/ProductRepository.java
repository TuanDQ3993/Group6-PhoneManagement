package com.example.PhoneManagement.repository;

import com.example.PhoneManagement.entity.Products;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface ProductRepository extends JpaRepository<Products, Integer> {
    @Override
    long count();

    @Query(value = "select n from Products n")
    List<Products> getListProduct();

    Page<Products> findByCategoryCategoryId(int categoryId, Pageable pageable);

    @Query("SELECT p FROM Products p ORDER BY p.createdAt DESC")
    Page<Products> findTop8ByOrderByCreatedAtDesc(Pageable pageable);

    @Query("SELECT p FROM Products p WHERE p.category.categoryId = :categoryId ORDER BY p.createdAt DESC")
    Page<Products> findTop8ByCategoryIdOrderByCreatedAtDesc(@Param("categoryId") int categoryId, Pageable pageable);

}