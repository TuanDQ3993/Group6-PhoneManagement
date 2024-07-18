package com.example.PhoneManagement.service;

import com.example.PhoneManagement.entity.Category;
import com.example.PhoneManagement.repository.CategoryRepository;
import com.example.PhoneManagement.repository.ColorRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CategoryService {
    CategoryRepository categoryRepository;
    public List<Category> findAllCategory(){
        return categoryRepository.findAll();
    }
}
