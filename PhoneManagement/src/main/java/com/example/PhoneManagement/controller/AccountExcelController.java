package com.example.PhoneManagement.controller;

import com.example.PhoneManagement.entity.Users;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;
import java.security.Timestamp;
import java.text.SimpleDateFormat;
import java.util.List;

public class AccountExcelController {
    private XSSFWorkbook workbook;
    private XSSFSheet sheet;
    private List<Users> listUsers;



    public AccountExcelController(List<Users> listUsers) {
        this.listUsers = listUsers;
        workbook = new XSSFWorkbook();
    }

    private void writeHeaderLine() {
        sheet = workbook.createSheet("Account_List");

        Row row = sheet.createRow(0);

        CellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setBold(true);
        font.setFontHeight(14);
        style.setFont(font);

        createCell(row, 0, "User ID", style);
        createCell(row, 1, "E-mail", style);
        createCell(row, 2, "Full Name", style);
        createCell(row, 3, "Address", style);
        createCell(row, 4, "PhoneNumber", style);
        createCell(row, 5, "Status", style);
        createCell(row, 6, "Created_At", style);
        createCell(row, 7, "Role", style);

    }

    private void createCell(Row row, int columnCount, Object value, CellStyle style) {
        sheet.autoSizeColumn(columnCount);
        Cell cell = row.createCell(columnCount);
        if (value instanceof Integer) {
            cell.setCellValue((Integer) value);
        } else if (value instanceof Boolean) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            cell.setCellValue(sdf.format((Timestamp) value));
        } else {
            cell.setCellValue((String) value);
        }
        cell.setCellStyle(style);
    }

    private void writeDataLines() {
        int rowCount = 1;

        CellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setFontHeight(12);
        style.setFont(font);

        for (Users user : listUsers) {
            Row row = sheet.createRow(rowCount++);
            int columnCount = 0;
            String status = "";
            if (user.isActive() == true) {
                status = "Hoạt động";
            } else {
                status = "Ngừng hoạt động";
            }
            createCell(row, columnCount++, user.getUserId(), style);
            createCell(row, columnCount++, user.getUserName(), style);
            createCell(row, columnCount++, user.getFullName(), style);
            createCell(row, columnCount++, user.getAddress(), style);
            createCell(row, columnCount++, user.getPhoneNumber(), style);
            createCell(row, columnCount++, status, style);
            createCell(row, columnCount++, user.getCreatedAt().toString(), style);
            createCell(row, columnCount++, user.getRole().getRoleName(), style);

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
