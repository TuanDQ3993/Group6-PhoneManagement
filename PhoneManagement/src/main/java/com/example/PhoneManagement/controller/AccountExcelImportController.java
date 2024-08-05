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


    public List<Users> importUsersFromExcel(InputStream inputStream) throws IOException {
        Map<String, Integer> roleMap = new HashMap<>();
        // Đoạn mã này có thể lấy danh sách các roles từ cơ sở dữ liệu
        List<Roles> rolesList = roleServiceImp.getAllRoles();
        for (Roles role : rolesList) {
            roleMap.put(role.getRoleName().toUpperCase(), role.getRoleId());
        }
        List<Users> usersList = new ArrayList<>();
        Workbook workbook = new XSSFWorkbook(inputStream);
        Sheet sheet = workbook.getSheetAt(0); // Assuming the data is in the first sheet

        for (int i = 1; i < sheet.getPhysicalNumberOfRows(); i++) { // Skipping the header row
            Row row = sheet.getRow(i);
            if (row == null) continue; // Check if row is null

            Users user = new Users();
            // user.setUserId() không cần thiết vì sẽ tự động tăng

            if (row.getCell(0) != null) {
                user.setUserName(row.getCell(0).getStringCellValue());
            }

            if (row.getCell(1) != null) {
                user.setFullName(row.getCell(1).getStringCellValue());
            }

            if (row.getCell(2) != null) {
                user.setAddress(row.getCell(2).getStringCellValue());
            }

            if (row.getCell(3) != null) {
                user.setPhoneNumber(row.getCell(3).getStringCellValue());
            }

            user.setCreatedAt(new Date());

            if (row.getCell(4) != null && row.getCell(4).getCellType() == CellType.STRING) {
                String roleName = row.getCell(4).getStringCellValue().trim().toUpperCase();
                Integer roleId = roleMap.get(roleName);

                if (roleId != null) {
                    Roles role = roleServiceImp.getRoleById(roleId);
                    if (role != null) {
                        user.setRole(role);
                    } else {
                        System.out.println("Role not found for roleId: " + roleId);
                        user.setRole(roleServiceImp.getDefaultRole());
                    }
                } else {
                    System.out.println("Invalid role name or ID for: " + roleName);
                    user.setRole(roleServiceImp.getDefaultRole());
                }
            }

            user.setActive(true);
            user.setPassword(passwordEncoder.encode("123456"));
            usersList.add(user);
        }
        workbook.close();
        return usersList;
    }

}
