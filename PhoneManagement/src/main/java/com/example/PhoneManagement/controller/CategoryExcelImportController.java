package com.example.PhoneManagement.controller;

import com.example.PhoneManagement.entity.Category;
import com.example.PhoneManagement.entity.Roles;
import com.example.PhoneManagement.entity.Users;
import com.example.PhoneManagement.service.imp.CategoryService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Controller;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

@Controller
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CategoryExcelImportController {
    CategoryService categoryService;

    public List<Category> importCategoryFromExcel(InputStream inputStream) throws IOException {
        List<Category> categoryList = new ArrayList<>();
        Set<String> existingCategoryNamesInExcel = new HashSet<>();
        Workbook workbook = new XSSFWorkbook(inputStream);
        Sheet sheet = workbook.getSheetAt(0);

        for (int i = 1; i < sheet.getPhysicalNumberOfRows(); i++) {
            Row row = sheet.getRow(i);
            if (row == null) continue;

            String categoryName = row.getCell(0) != null ? row.getCell(0).getStringCellValue() : null;
            if (categoryName == null || categoryName.isEmpty()) continue;

            if (existingCategoryNamesInExcel.contains(categoryName)) {
                System.out.println("Duplicate category name in Excel: " + categoryName + ", skipping...");
                continue;
            }

            boolean existingCategory = categoryService.findByName(categoryName);

            if (existingCategory) {
                System.out.println("Category with name " + categoryName + " already exists in database, skipping...");
            } else {
                Category category = new Category();
                category.setCategoryName(categoryName);

                if (row.getCell(1) != null && row.getCell(1).getCellType() == CellType.BOOLEAN) {
                    category.setDeleted(row.getCell(1).getBooleanCellValue());
                }

                categoryList.add(category);
                existingCategoryNamesInExcel.add(categoryName);
            }
        }
        workbook.close();
        return categoryList;
    }


}
