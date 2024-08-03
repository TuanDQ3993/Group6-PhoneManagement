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

    Page<Products> findByCategoryCategoryId(Integer categoryId, Pageable pageable);
    Page<Products> findByProductNameContainingIgnoreCase(String name, Pageable pageable);
    Page<Products> findByCategoryCategoryIdAndProductNameContainingIgnoreCase(Integer categoryId, String name, Pageable pageable);



}