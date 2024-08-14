package com.example.PhoneManagement.controller;

import com.example.PhoneManagement.entity.Roles;
import com.example.PhoneManagement.entity.Users;
import com.example.PhoneManagement.service.AccountServiceImp;
import com.example.PhoneManagement.service.RoleServiceImp;
import com.example.PhoneManagement.service.UserServiceImp;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.*;

@Controller
public class AccountExcelImportController {

    @Autowired
    private AccountServiceImp accountServiceImp;
    @Autowired
    private RoleServiceImp roleServiceImp;
    @Autowired
    private PasswordEncoder passwordEncoder;


    public List<Users> importUsersFromExcel(InputStream inputStream, List<String> errorMessages) throws IOException {
        Map<String, Integer> roleMap = new HashMap<>();
        Map<Integer, Roles> cachedRoles = new HashMap<>();
        List<Roles> rolesList = roleServiceImp.getAllRoles();
        for (Roles role : rolesList) {
            roleMap.put(role.getRoleName().toUpperCase(), role.getRoleId());
        }

        List<Users> usersList = new ArrayList<>();
        Workbook workbook = new XSSFWorkbook(inputStream);
        Sheet sheet = workbook.getSheetAt(0);

        Row headerRow = sheet.getRow(0);
        if (headerRow == null || headerRow.getPhysicalNumberOfCells() != 5) {
            workbook.close();
            throw new IOException("Invalid Excel file: Incorrect number of columns.");
        }


        String[] expectedHeaders = {"Email", "Full Name", "Address", "PhoneNumber", "Role"};
        for (int j = 0; j < expectedHeaders.length; j++) {
            if (!headerRow.getCell(j).getStringCellValue().trim().equalsIgnoreCase(expectedHeaders[j])) {
                workbook.close();
                throw new IOException("Invalid Excel file: Column " + (j + 1) + " should be '" + expectedHeaders[j] + "'.");
            }
        }

        for (int i = 1; i < sheet.getPhysicalNumberOfRows(); i++) {
            Row row = sheet.getRow(i);
            if (row == null) continue;

            Users user = new Users();
            String userName = null;
            String phoneNumber = null;

            if (row.getCell(0) != null) {
                userName = row.getCell(0).getStringCellValue();
                if (accountServiceImp.isEmailExist(userName)) {
                    throw new IOException("Row " + (i + 1) + ": Email " + userName + " already exists.");
                }
                user.setUserName(userName);
            } else {
                throw new IOException("Row " + (i + 1) + ": Email is required.");

            }

            if (row.getCell(3) != null) {
                phoneNumber = row.getCell(3).getStringCellValue();
                if (accountServiceImp.isPhoneExist(phoneNumber) && !row.getCell(4).getStringCellValue().equalsIgnoreCase("USER")) {
                    throw new IOException("Row " + (i + 1) + ": Phone number " + phoneNumber + " already exists.");

                }
                user.setPhoneNumber(phoneNumber);
            } else {
                throw new IOException("Row " + (i + 1) + ": Phone number is required.");

            }

            user.setFullName(row.getCell(1) != null ? row.getCell(1).getStringCellValue() : null);
            user.setAddress(row.getCell(2) != null ? row.getCell(2).getStringCellValue() : null);
            user.setCreatedAt(new Date());

            if (row.getCell(4) != null && row.getCell(4).getCellType() == CellType.STRING) {
                String roleName = row.getCell(4).getStringCellValue().trim().toUpperCase();
                Integer roleId = roleMap.get(roleName);

                Roles role;
                if (roleId != null) {
                    role = cachedRoles.get(roleId);
                    if (role == null) {
                        role = roleServiceImp.getRoleById(roleId);
                        cachedRoles.put(roleId, role);
                    }
                } else {
                    role = roleServiceImp.getDefaultRole();
                }
                user.setRole(role);
            } else {
                user.setRole(roleServiceImp.getDefaultRole());
            }

            user.setActive(true);
            user.setPassword(passwordEncoder.encode("123456"));
            usersList.add(user);
        }

        workbook.close();
        return usersList;
    }






}
