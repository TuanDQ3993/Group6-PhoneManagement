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
        createCell(row, 2, "Image", style);
        createCell(row, 3, "Issues", style);
        createCell(row, 4, "Status", style);
        createCell(row, 5, "Repair Date", style);
        createCell(row, 6, "Technical Name", style);
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
            addImageToCell(row, columnCount++, warrantyRepair.getImage());
            createCell(row, columnCount++, warrantyRepair.getIssueDescription(), style);
            createCell(row, columnCount++, warrantyRepair.getStatus(), style);
            createCell(row, columnCount++, warrantyRepair.getRepairDate(), style);
            createCell(row, columnCount++, warrantyRepair.getTechnical().getFullName(), style);
        }
    }

    private void addImageToCell(Row row, int columnCount, String imageName) throws IOException {
        int fixedWidthInPixels = 200; // Chiều rộng cố định của ảnh
        int fixedHeightInPoints = 200; // Chiều cao cố định của ảnh

        sheet.setColumnWidth(columnCount, (int) (fixedWidthInPixels / 8.43 * 256)); // Đặt chiều rộng cột
        row.setHeightInPoints(fixedHeightInPoints * 0.75f); // Đặt chiều cao hàng

        int pictureIdx;

        // Đường dẫn đến tệp ảnh
        String imagePath = "C:/Users/Admin/Downloads/Group6-PhoneManagement-main/PhoneManagement/uploads/" + imageName;

        // Tải ảnh từ hệ thống tệp
        byte[] bytes;
        try (FileInputStream inputStream = new FileInputStream(imagePath)) {
            bytes = IOUtils.toByteArray(inputStream);
            pictureIdx = workbook.addPicture(bytes, Workbook.PICTURE_TYPE_PNG);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        Drawing<?> drawing = sheet.createDrawingPatriarch();
        CreationHelper helper = workbook.getCreationHelper();

        // Tính toán vị trí neo
        ClientAnchor anchor = helper.createClientAnchor();
        anchor.setCol1(columnCount);
        anchor.setRow1(row.getRowNum());
        anchor.setCol2(columnCount + 1);
        anchor.setRow2(row.getRowNum() + 1);

        // Tạo ảnh và điều chỉnh kích thước để vừa với kích thước cố định của ô
        Picture picture = drawing.createPicture(anchor, pictureIdx);
        picture.resize();

        // Điều chỉnh lại tỷ lệ để ảnh có kích thước cố định
        double imageWidthInPixels = picture.getImageDimension().getWidth();
        double imageHeightInPixels = picture.getImageDimension().getHeight();

        double widthRatio = fixedWidthInPixels / imageWidthInPixels;
        double heightRatio = (fixedHeightInPoints * 0.75) / imageHeightInPixels; // 0.75 là hệ số chuyển đổi từ điểm sang pixel

        double resizeRatio = Math.min(widthRatio, heightRatio);

        picture.resize(resizeRatio);
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
