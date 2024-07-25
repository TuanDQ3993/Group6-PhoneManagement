package com.example.PhoneManagement.repository;

import com.example.PhoneManagement.entity.Key.KeyProductColor;
import com.example.PhoneManagement.entity.ProductColor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductColorRepository extends JpaRepository<ProductColor, Integer> {
}
