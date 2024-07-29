package com.example.PhoneManagement.util;

import com.example.PhoneManagement.dto.request.OrderInfoDTO;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.util.IOUtils;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;


import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderExcelExporter {
    XSSFWorkbook workbook;
    XSSFSheet sheet;

    List<OrderInfoDTO> listOrders;

    public OrderExcelExporter(List<OrderInfoDTO> listOrders) {
        this.listOrders = listOrders;
        workbook = new XSSFWorkbook();
        sheet = workbook.createSheet("Orders");
    }

    private void writeHeaderRow() {
        Row row = sheet.createRow(0);

        CellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setBold(true);
        font.setFontHeight(16);
        style.setFont(font);

        Cell cell = row.createCell(0);
        cell.setCellValue("Order ID");
        cell.setCellStyle(style);

        cell = row.createCell(1);
        cell.setCellValue("Products");
        cell.setCellStyle(style);

        cell = row.createCell(2);
        cell.setCellValue("Total amount($)");
        cell.setCellStyle(style);

        cell = row.createCell(3);
        cell.setCellValue("Order date");
        cell.setCellStyle(style);

        cell = row.createCell(4);
        cell.setCellValue("Purchaser");
        cell.setCellStyle(style);

        cell = row.createCell(5);
        cell.setCellValue("Status");
        cell.setCellStyle(style);


    }

    private void writeDataRow() {
        int rowCount = 1;

        CellStyle dateCellStyle = workbook.createCellStyle();
        short dateFormat = workbook.createDataFormat().getFormat("yyyy-MM-dd");
        dateCellStyle.setDataFormat(dateFormat);

        for (OrderInfoDTO order : listOrders) {
            Row row = sheet.createRow(rowCount++);



            Cell cell = row.createCell(0);
            cell.setCellValue(order.getOrderID());
            sheet.autoSizeColumn(0);

            cell = row.createCell(1);
            if (order.getCountP() > 0) {
                cell.setCellValue(order.getProductName() + " and " + order.getCountP() + " Products");
                sheet.autoSizeColumn(1);
            } else {
                cell.setCellValue(order.getProductName());
                sheet.autoSizeColumn(1);
            }

            cell = row.createCell(2);
            cell.setCellValue(order.getTotalAmount());
            sheet.autoSizeColumn(2);

            cell = row.createCell(3);
            cell.setCellValue(order.getOrderDate());
            cell.setCellStyle(dateCellStyle);
            sheet.autoSizeColumn(3);

            cell = row.createCell(4);
            cell.setCellValue(order.getUsername());
            sheet.autoSizeColumn(4);

            cell = row.createCell(5);
            cell.setCellValue(order.getStatus());
            sheet.autoSizeColumn(5);

//            if (order.getImage() != null && !order.getImage().isEmpty()) {
//                try (InputStream imageStream = getImageInputStream(order.getImage())) {
//                    addImageToSheet(imageStream, rowCount - 1, 6);
//                } catch (IOException e) {
//                    throw new RuntimeException(e);
//                }
//            }
        }
    }

    private InputStream getImageInputStream(String imagePath) throws IOException {

        URL url = new URL(imagePath);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.connect();
        return connection.getInputStream();
    }

    private void addImageToSheet(InputStream imageStream, int rowIndex, int columnIndex) throws IOException {
        // Load image
        byte[] imageBytes = IOUtils.toByteArray(imageStream);

        // Add image to the workbook
        int pictureIndex = workbook.addPicture(imageBytes, Workbook.PICTURE_TYPE_PNG);
        CreationHelper helper = workbook.getCreationHelper();
        Drawing drawing = sheet.createDrawingPatriarch();
        ClientAnchor anchor = helper.createClientAnchor();

        anchor.setCol1(columnIndex);
        anchor.setRow1(rowIndex);
        anchor.setCol2(columnIndex + 1);
        anchor.setRow2(rowIndex + 1);

        // Create the picture
        Picture picture = drawing.createPicture(anchor, pictureIndex);
        picture.resize(1.0);
    }

    public void export(HttpServletResponse response) throws IOException {
        writeHeaderRow();
        writeDataRow();

        ServletOutputStream outputStream = response.getOutputStream();
        workbook.write(outputStream);
        workbook.close();
        outputStream.close();
    }


}
