package com.example.PhoneManagement.service;

import com.example.PhoneManagement.dto.request.CategoryDTO;
import com.example.PhoneManagement.entity.Category;
import com.example.PhoneManagement.repository.CategoryRepository;
import com.example.PhoneManagement.service.imp.CategoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CategoryServiceImpTest {

    @InjectMocks
    private CategoryServiceImp categoryService;

    @Mock
    private CategoryRepository categoryRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testFindAllCategory() {

        Pageable pageable = Pageable.ofSize(10);
        List<Category> categories = Arrays.asList(new Category(), new Category());
        Page<Category> categoryPage = new PageImpl<>(categories, pageable, categories.size());

        when(categoryRepository.findAll(pageable)).thenReturn(categoryPage);


        Page<Category> result = categoryService.findAllCategory(pageable);


        assertEquals(2, result.getContent().size());
        verify(categoryRepository, times(1)).findAll(pageable);
    }

    @Test
    void testGetAllCategoryActive() {

        Category activeCategory = new Category();
        activeCategory.setDeleted(true);

        Category inactiveCategory = new Category();
        inactiveCategory.setDeleted(false);

        when(categoryRepository.findAll()).thenReturn(Arrays.asList(activeCategory, inactiveCategory));


        List<Category> result = categoryService.getAllCategoryActive();


        assertEquals(1, result.size());
        assertTrue(result.get(0).isDeleted());
    }

    @Test
    void testAddCategory_Success() {

        CategoryDTO categoryDTO = new CategoryDTO();
        categoryDTO.setCateName("New Category");
        when(categoryRepository.findAll()).thenReturn(Collections.emptyList());


        categoryService.addCategory(categoryDTO);


        verify(categoryRepository, times(1)).save(any(Category.class));
    }

    @Test
    void testAddCategory_ExistingName() {

        CategoryDTO categoryDTO = new CategoryDTO();
        categoryDTO.setCateName("Existing Category");
        Category existingCategory = new Category();
        existingCategory.setCategoryName("Existing Category");

        when(categoryRepository.findAll()).thenReturn(Arrays.asList(existingCategory));

        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            categoryService.addCategory(categoryDTO);
        });
        assertEquals("Category Name already existed", thrown.getMessage());
    }

    @Test
    void testEditCategory_Success() {

        int cateId = 1;
        CategoryDTO categoryDTO = new CategoryDTO();
        categoryDTO.setCateName("Updated Category");
        Category category = new Category();
        category.setCategoryName("Old Category");

        when(categoryRepository.findById(cateId)).thenReturn(Optional.of(category));
        when(categoryRepository.findAll()).thenReturn(Collections.emptyList());


        categoryService.editCategory(cateId, categoryDTO);


        assertEquals("Updated Category", category.getCategoryName());
        verify(categoryRepository, times(1)).save(category);
    }

    @Test
    void testDeleteCategory() {

        int cateId = 1;
        Category category = new Category();
        category.setDeleted(false);

        when(categoryRepository.findById(cateId)).thenReturn(Optional.of(category));


        categoryService.deleteCategory(cateId);


        assertTrue(category.isDeleted());
        verify(categoryRepository, times(1)).save(category);
    }

    @Test
    void testFindByName_Exists() {

        String cateName = "Existing Category";
        Category category = new Category();
        category.setCategoryName(cateName);

        when(categoryRepository.findAll()).thenReturn(Arrays.asList(category));


        boolean exists = categoryService.findByName(cateName);


        assertTrue(exists);
    }

    @Test
    void testFindAll() {

        Category category1 = new Category();
        Category category2 = new Category();
        when(categoryRepository.findAll()).thenReturn(Arrays.asList(category1, category2));


        List<Category> result = categoryService.findAll();


        assertEquals(2, result.size());
    }
}
