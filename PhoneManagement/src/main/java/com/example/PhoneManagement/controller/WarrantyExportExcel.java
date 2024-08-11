package com.example.PhoneManagement.controller;

import com.example.PhoneManagement.entity.WarrantyRepair;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.util.IOUtils;
import org.apache.poi.xssf.usermodel.*;

import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;

public class WarrantyExportExcel {
    private XSSFWorkbook workbook;
    private XSSFSheet sheet;
    private final List<WarrantyRepair> warrantyRepairList;

    public WarrantyExportExcel(List<WarrantyRepair> warrantyRepairList) {
        this.warrantyRepairList = warrantyRepairList;
        this.workbook = new XSSFWorkbook();
    }

    private void writeHeaderLine() {
        sheet = workbook.createSheet("Danh sách bảo hành sản phẩm(Tải về)");

        Row row = sheet.createRow(0);

        CellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setBold(true);
        font.setFontHeight(14);
        style.setFont(font);

        createCell(row, 0, "Customer Name", style);
        createCell(row, 1, "Product Name", style);
        createCell(row, 2, "Issues", style);
        createCell(row, 3, "Status", style);
        createCell(row, 4, "Repair Date", style);
        createCell(row, 5, "Date Complete", style);
        createCell(row, 6, "Note to customer", style);
        createCell(row, 7, "Technical Name", style);
    }

    private void createCell(Row row, int columnCount, Object value, CellStyle style) {
        sheet.autoSizeColumn(columnCount);
        Cell cell = row.createCell(columnCount);
        if (value instanceof Integer) {
            cell.setCellValue((Integer) value);
        } else if (value instanceof java.util.Date) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            cell.setCellValue(sdf.format((java.util.Date) value));
        } else if (value instanceof String) {
            cell.setCellValue((String) value);
        }
        cell.setCellStyle(style);
    }

    private void writeDataLines() throws IOException {
        int rowCount = 1;

        CellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setFontHeight(12);
        style.setFont(font);

        for (WarrantyRepair warrantyRepair : warrantyRepairList) {
            Row row = sheet.createRow(rowCount++);
            int columnCount = 0;

            // Assuming getUser().getUserName() retrieves the customer name
            createCell(row, columnCount++, warrantyRepair.getUser().getFullName(), style);
            createCell(row, columnCount++, warrantyRepair.getProductName(), style);
            createCell(row, columnCount++, warrantyRepair.getIssueDescription(), style);
            createCell(row, columnCount++, warrantyRepair.getStatus(), style);
            createCell(row, columnCount++, warrantyRepair.getRepairDate(), style);
            createCell(row, columnCount++, warrantyRepair.getDate_completed(), style);
            createCell(row, columnCount++, warrantyRepair.getNoteTechnical(), style);
            createCell(row, columnCount++, warrantyRepair.getTechnical().getFullName(), style);
        }
    }


    public void export(HttpServletResponse response) throws IOException {
        writeHeaderLine();
        writeDataLines();

        ServletOutputStream outputStream = response.getOutputStream();
        workbook.write(outputStream);
        workbook.close();

        outputStream.close();
    }
}
