package com.example.PhoneManagement.service;

import com.example.PhoneManagement.dto.request.CategoryDTO;
import com.example.PhoneManagement.entity.Category;
import com.example.PhoneManagement.repository.CategoryRepository;
import com.example.PhoneManagement.service.imp.CategoryService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CategoryServiceImp implements CategoryService {
    CategoryRepository categoryRepository;
    @Override
    public Page<Category> findAllCategory(Pageable pageable){
        return categoryRepository.findAll(pageable);
    }

    @Override
    public List<Category> getAllCategoryActive() {
        return categoryRepository.findAll().stream().filter(active -> active.isDeleted()).toList();
    }

    @Override
    public void addCategory(CategoryDTO categoryDTO) {
        Category category=new Category();
        boolean cate=categoryRepository.findAll().stream().anyMatch(cName -> cName.getCategoryName().equals(categoryDTO.getCateName()));
        if(cate) throw new IllegalArgumentException("Category Name already existed");
        if(categoryDTO.getCateName().length()>100) throw new IllegalArgumentException("Category Name no more than 100 characters");
        if(categoryDTO.getCateName().trim().isEmpty()) throw new IllegalArgumentException("category name cannot be empty");
        category.setCategoryName(categoryDTO.getCateName());
        categoryRepository.save(category);
    }

    @Override
    public void editCategory(int cateId, CategoryDTO categoryDTO) {
        Category category=categoryRepository.findById(cateId).orElseThrow(()->new RuntimeException("Category not existed"));
        boolean cate=categoryRepository.findAll().stream().anyMatch(cName -> cName.getCategoryName().equals(categoryDTO.getCateName()));
        if(cate && !category.getCategoryName().equals(categoryDTO.getCateName())) return;
        if(categoryDTO.getCateName().length()>100) return;
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

    @Override
    public boolean findByName(String cateName) {
        return categoryRepository.findAll().stream().anyMatch(cate -> cate.getCategoryName().equals(cateName));
    }

    @Override
    public List<Category> findAll() {
        return categoryRepository.findAll();
    }


}
