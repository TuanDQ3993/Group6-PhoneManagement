package com.example.PhoneManagement.controller;

import com.example.PhoneManagement.entity.Category;
import com.example.PhoneManagement.entity.Roles;
import com.example.PhoneManagement.entity.Users;
import com.example.PhoneManagement.service.imp.CategoryService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.poi.ss.usermodel.*;
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

        // Check if the first row (header) contains only one column named 'categoryName'
        Row headerRow = sheet.getRow(0);
        if (headerRow == null || headerRow.getPhysicalNumberOfCells() != 1) {
            workbook.close();
            throw new IllegalArgumentException("The Excel file must contain only one column named 'categoryName'.");
        }

        String firstCellHeader = headerRow.getCell(0) != null ? headerRow.getCell(0).getStringCellValue() : null;

        if (!"categoryName".equalsIgnoreCase(firstCellHeader)) {
            workbook.close();
            throw new IllegalArgumentException("The Excel file does not contain the expected column: 'categoryName'.");
        }

        for (int i = 1; i < sheet.getPhysicalNumberOfRows(); i++) {
            Row row = sheet.getRow(i);
            if (row == null) continue;

            Cell cell = row.getCell(0);
            if (cell == null) continue;

            String categoryName = null;

            // Handle different cell types
            if (cell.getCellType() == CellType.STRING) {
                categoryName = cell.getStringCellValue();
            } else if (cell.getCellType() == CellType.NUMERIC) {
                categoryName = String.valueOf((int) cell.getNumericCellValue()); // Convert numeric to string
            } else {
                continue; // Skip if the cell is of an unexpected type
            }

            // Trim whitespace and check if the categoryName is empty
            if (categoryName == null || categoryName.trim().isEmpty()) continue;

            if (existingCategoryNamesInExcel.contains(categoryName.trim())) {
                System.out.println("Duplicate category name in Excel: " + categoryName.trim() + ", skipping...");
                continue;
            }

            boolean existingCategory = categoryService.findByName(categoryName.trim());

            if (existingCategory) {
                System.out.println("Category with name " + categoryName.trim() + " already exists in database, skipping...");
            } else {
                Category category = new Category();
                category.setCategoryName(categoryName.trim());
                category.setDeleted(false);  // Set isDeleted to false (0)

                categoryList.add(category);
                existingCategoryNamesInExcel.add(categoryName.trim());
            }
        }
        workbook.close();
        return categoryList;
    }

}
