package com.example.PhoneManagement.service.imp;

import com.example.PhoneManagement.dto.request.CategoryDTO;
import com.example.PhoneManagement.entity.Category;
import com.example.PhoneManagement.repository.CategoryRepository;
import com.example.PhoneManagement.repository.ColorRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;


public interface CategoryService {
    public Page<Category> findAllCategory(Pageable pageable);

    List<Category> getAllCategoryActive();

    List<Category> getAllCategoryActive();

    void addCategory( CategoryDTO categoryDTO);

    void editCategory(int cateID, CategoryDTO categoryDTO);

    void deleteCategory(int cateId);

    void saveAll(List<Category> categories);

    boolean findByName(String cateName);

    public List<Category> findAll();
}
