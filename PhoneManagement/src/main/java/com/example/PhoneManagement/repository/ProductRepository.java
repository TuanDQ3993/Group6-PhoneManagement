package com.example.PhoneManagement.repository;

import com.example.PhoneManagement.entity.ProductColor;
import com.example.PhoneManagement.entity.Products;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface ProductRepository extends JpaRepository<Products, Integer> {
    @Override
    long count();

    @Query(value = "select n from Products n")
    List<Products> getListProduct();

    @Query(value = "select ten from Products where id=: id", nativeQuery = true)
    String getNameById(@Param("id") int id);

    Page<Products> findByCategoryCategoryId(int categoryId, Pageable pageable);
}