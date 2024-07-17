package com.example.PhoneManagement.repository;

import com.example.PhoneManagement.entity.Colors;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ColorRepository extends JpaRepository<Colors,Integer> {
}
