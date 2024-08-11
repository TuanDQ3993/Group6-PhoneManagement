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


    public List<Users> importUsersFromExcel(InputStream inputStream, Model model) throws IOException {
        Map<String, Integer> roleMap = new HashMap<>();
        Map<Integer, Roles> cachedRoles = new HashMap<>(); // Cache các role đã truy xuất
        List<Roles> rolesList = roleServiceImp.getAllRoles();
        for (Roles role : rolesList) {
            roleMap.put(role.getRoleName().toUpperCase(), role.getRoleId());
        }

        List<Users> usersList = new ArrayList<>();
        List<String> errorMessages = new ArrayList<>();
        Workbook workbook = new XSSFWorkbook(inputStream);
        Sheet sheet = workbook.getSheetAt(0);

        for (int i = 1; i < sheet.getPhysicalNumberOfRows(); i++) {
            Row row = sheet.getRow(i);
            if (row == null) continue;

            Users user = new Users();
            String userName = null;
            String phoneNumber = null;

            if (row.getCell(0) != null) {
                userName = row.getCell(0).getStringCellValue();
                if (accountServiceImp.isEmailExist(userName)) {
                    errorMessages.add("Row " + (i + 1) + ": Email " + userName + " already exists.");
                    continue;
                }
                user.setUserName(userName);
            } else {
                errorMessages.add("Row " + (i + 1) + ": Email is required.");
                continue;
            }

            if (row.getCell(3) != null) {
                phoneNumber = row.getCell(3).getStringCellValue();
                if (accountServiceImp.isPhoneExist(phoneNumber)) {
                    errorMessages.add("Row " + (i + 1) + ": Phone number " + phoneNumber + " already exists.");
                    continue;
                }
                user.setPhoneNumber(phoneNumber);
            } else {
                errorMessages.add("Row " + (i + 1) + ": Phone number is required.");
                continue;
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
        model.addAttribute("errorMessages", errorMessages);
        return usersList;
    }





}
