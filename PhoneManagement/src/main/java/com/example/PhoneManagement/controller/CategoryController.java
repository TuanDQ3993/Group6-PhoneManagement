package com.example.PhoneManagement.controller;

import com.example.PhoneManagement.dto.request.CategoryDTO;
import com.example.PhoneManagement.dto.request.ProductViewRequest;
import com.example.PhoneManagement.entity.Category;
import com.example.PhoneManagement.entity.Users;
import com.example.PhoneManagement.service.imp.CategoryService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/category")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CategoryController {
    CategoryService categoryService;
    CategoryExcelImportController categoryExcelImportController;

    @GetMapping
    public String getCategory(Model model, @RequestParam(defaultValue = "0") int page,
                              @RequestParam(defaultValue = "5") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("categoryId").descending());
        Page<Category> categoryPage = categoryService.findAllCategory(pageable);
        model.addAttribute("cates", categoryPage);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", categoryPage.getTotalPages());
        model.addAttribute("pageSize", size);
        return "category";
    }

    @PostMapping()
    public String addCategory(@RequestParam("name") String name,RedirectAttributes redirectAttributes) {
        CategoryDTO categoryDTO = new CategoryDTO();
        categoryDTO.setCateName(name);
        try {
            categoryService.addCategory(categoryDTO);
            redirectAttributes.addFlashAttribute("successMessage", "Category update successfully!");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "An unexpected error occurred.");
        }
        return "redirect:/admin/category";
    }

    @PostMapping("/{cateId}")
    public String editCategory(@RequestParam("categoryName") String name, @PathVariable("cateId") int cateId, RedirectAttributes redirectAttributes) {
        CategoryDTO categoryDTO = new CategoryDTO();
        categoryDTO.setCateId(cateId);
        categoryDTO.setCateName(name);
        try {
            categoryService.editCategory(cateId, categoryDTO);
            redirectAttributes.addFlashAttribute("successMessage", "Category update successfully!");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "An unexpected error occurred.");
        }
        return "redirect:/admin/category";
    }

    @PostMapping("/delete")
    public String deleteCategory(@RequestParam("cateId") int cateId) {
        categoryService.deleteCategory(cateId);
        return "redirect:/admin/category";
    }

    @PostMapping("/importCate")
    public String importCategory(@RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes) {
        String contentType = file.getContentType();
        String fileName = file.getOriginalFilename();

        if (contentType == null || !isExcelFile(contentType, fileName)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Please upload a valid Excel file.");
            return "redirect:/admin/category";
        }

        try {
            List<Category> categoryList = categoryExcelImportController.importCategoryFromExcel(file.getInputStream());
            categoryService.saveAll(categoryList);
        } catch (IOException e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("errorMessage", "Error processing file.");
            return "redirect:/admin/category";
        }

        redirectAttributes.addFlashAttribute("successMessage", "Categories imported successfully.");
        return "redirect:/admin/category";
    }

    private boolean isExcelFile(String contentType, String fileName) {
        return ("application/vnd.ms-excel".equals(contentType) ||
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet".equals(contentType) ||
                fileName.endsWith(".xls") || fileName.endsWith(".xlsx"));
    }

}
