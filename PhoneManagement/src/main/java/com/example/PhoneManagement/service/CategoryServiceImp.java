package com.example.PhoneManagement.service;

import com.example.PhoneManagement.dto.request.CategoryDTO;
import com.example.PhoneManagement.entity.Category;
import com.example.PhoneManagement.repository.CategoryRepository;
import com.example.PhoneManagement.service.imp.CategoryService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CategoryServiceImp implements CategoryService {
    CategoryRepository categoryRepository;
    @Override
    public List<Category> findAllCategory(){
        return categoryRepository.findAll();
    }

    @Override
    public void addCategory(CategoryDTO categoryDTO) {
        Category category=new Category();
        category.setCategoryName(categoryDTO.getCateName());
        categoryRepository.save(category);
    }

    @Override
    public void editCategory(int cateId, CategoryDTO categoryDTO) {
        Category category=categoryRepository.findById(cateId).orElseThrow(()->new RuntimeException("Category not existed"));
        category.setCategoryName(categoryDTO.getCateName());
        categoryRepository.save(category);
    }

    @Override
    public void deleteCategory(int cateId) {
        Category category=categoryRepository.findById(cateId).orElseThrow(()->new RuntimeException("Category not existed"));
        if(category.isDeleted()){
            category.setDeleted(false);
        }else category.setDeleted(true);
        categoryRepository.save(category);
    }

    @Override
    public void saveAll(List<Category> categories) {
        categoryRepository.saveAll(categories);
    }


}
