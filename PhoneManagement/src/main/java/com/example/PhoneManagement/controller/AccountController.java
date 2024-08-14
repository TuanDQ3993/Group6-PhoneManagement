package com.example.PhoneManagement.controller;

import com.example.PhoneManagement.dto.request.PageDTO;
import com.example.PhoneManagement.entity.Roles;
import com.example.PhoneManagement.entity.Users;
import com.example.PhoneManagement.service.ProductServiceImp;
import com.example.PhoneManagement.service.RoleServiceImp;
import com.example.PhoneManagement.service.AccountServiceImp;
import com.example.PhoneManagement.service.WarrantyServiceImp;
import com.example.PhoneManagement.service.imp.OrderService;
import org.springframework.data.domain.Pageable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.ui.Model;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


@Controller
@RequestMapping("/admin")
public class AccountController {

    @Autowired
    private AccountServiceImp accountServiceImp;
    @Autowired
    private RoleServiceImp roleServiceImp;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private AccountExcelImportController accountExcelImportController;
    @Autowired
    private WarrantyServiceImp warrantyRepairService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private ProductServiceImp productServiceImp;

    @GetMapping("dashboard")
    public String dashboard(Model model) {


        model.addAttribute("totalAccount", accountServiceImp.countAllUser());
        model.addAttribute("order", orderService.countOrdersInCurrentMonth());
        model.addAttribute("totalamount", orderService.getTotalAmountForCurrentMonth());
        model.addAttribute("totalProduct", productServiceImp.countAllProduct());
        model.addAttribute("sumByStatusByAdmin", warrantyRepairService.countAllByStatusByAdmin());
        model.addAttribute("sumAllByAdmin", warrantyRepairService.countAllByAdmin());
        return "dashboard_admin";
    }


    //Show all user with paging
    @GetMapping("/users")
    public String listOrSearchUsers(Model model,
                                    @RequestParam(defaultValue = "0") int page,
                                    @RequestParam(defaultValue = "5") int pageSize,
                                    @RequestParam(required = false) String query,
                                    @RequestParam(required = false) Integer role,
                                    Authentication authentication) {
        Users users = (Users) authentication.getPrincipal();
        int userId = users.getUserId();
        addUserPageAttributes(model, page, pageSize, query, role, userId);
        return "accountlist";
    }


    // View details each user
    @GetMapping("/viewUser/{userid}")
    public String showDetailsUser(Model model, @PathVariable int userid, Authentication authentication) {
        Users theUser = accountServiceImp.getUserById(userid);
        List<Roles> roles = roleServiceImp.getAllRoles();
        Users users = (Users) authentication.getPrincipal();
        int userId = users.getUserId();
        model.addAttribute("roles", roles);
        model.addAttribute("userId", userId);
        model.addAttribute("users", theUser);
        return "accountdetails";
    }

    // Ban user
    @PostMapping("/banUser/{userid}")
    public String banUser(Model model, @PathVariable int userid) {
        accountServiceImp.banUser(userid);
        return ("redirect:/admin/users");
    }


    // Unban user
    @PostMapping("/unBanUser/{userid}")
    public String unBanUser(@PathVariable int userid) {
        accountServiceImp.unBanUser(userid);
        return ("redirect:/admin/users");
    }


    // Change role of account
    @PostMapping("/changeRole/{id}")
    public String changeUserRole(@PathVariable int id, @RequestParam int roleId) {
        roleServiceImp.changeUserRole(id, roleId);
        return ("redirect:/admin/users");
    }

    private void addUserPageAttributes(Model model, int page, int pageSize, String query, Integer role, int userId) {
        PageDTO pageDTO = new PageDTO();
        pageDTO.setPageNumber(page);
        pageDTO.setPageSize(pageSize);
        Page<Users> userPage = null;

        if ((query != null && !query.isEmpty()) && (role != null && role > 0)) {
            // Tìm kiếm và lọc theo vai trò đồng thời
            userPage = accountServiceImp.searchAndFilterUsers(query, pageDTO, role);
        } else if (query != null && !query.isEmpty()) {
            // Chỉ tìm kiếm
            userPage = accountServiceImp.searchUsers(query, pageDTO);
        } else if (role != null && role > 0) {
            // Chỉ lọc theo vai trò
            userPage = accountServiceImp.filterRole(role, pageDTO);
        } else {
            // Không có tìm kiếm hay lọc, hiển thị tất cả
            userPage = accountServiceImp.findPaginated(pageDTO);
        }
        model.addAttribute("userPage", userPage);
        model.addAttribute("query", query);
        model.addAttribute("role", role);
        model.addAttribute("userId", userId);
        if (userPage != null) {
            int totalPages = userPage.getTotalPages();
            if (totalPages > 0) {
                List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPages)
                        .boxed()
                        .collect(Collectors.toList());
                model.addAttribute("pageNumbers", pageNumbers);
            }
        }
        model.containsAttribute("errorMessages");
        model.addAttribute("size", pageSize);
        model.addAttribute("userForm", new Users());
        model.addAttribute("rolesFilter", roleServiceImp.getAllRoles());
    }

    @PostMapping("/saveAccount")
    public String saveAccount(@ModelAttribute("userForm") Users user, Model model, Authentication authentication, RedirectAttributes redirectAttributes) {

        if (accountServiceImp.isPhoneExist(user.getPhoneNumber())) {
            model.addAttribute("phoneExistsError", "Phone number already exists. Try again.");
        }

        if (accountServiceImp.isEmailExist(user.getUsername())) {
            model.addAttribute("emailExistsError", "Email already exists. Try again.");
        }

        if (model.containsAttribute("phoneExistsError") || model.containsAttribute("emailExistsError")) {
            Users currentUser = (Users) authentication.getPrincipal();
            addUserPageAttributes(model, 0, 5, "", null, currentUser.getUserId());
            return "accountlist";
        }

        user.setCreatedAt(new Date());
        user.setActive(true);
        user.setPassword(passwordEncoder.encode("123456"));
        accountServiceImp.createUser(user);

        // Add success message to RedirectAttributes
        redirectAttributes.addFlashAttribute("success", "Account created successfully!");

        return "redirect:/admin/users";
    }

    @PostMapping("/importUsers")
    public String importExcel(@RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes) {
        if (file.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "No file selected. Please upload an Excel file.");
            return "redirect:/admin/users";
        }

        List<String> errorMessages = new ArrayList<>();
        try {
            List<Users> usersList = accountExcelImportController.importUsersFromExcel(file.getInputStream(), errorMessages);
            if (!usersList.isEmpty()) {
                accountServiceImp.saveAll(usersList);
                redirectAttributes.addFlashAttribute("success", "Users imported successfully!");
            } else {
                redirectAttributes.addFlashAttribute("error", "Import Fail");
            }
        } catch (IOException e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }

        return "redirect:/admin/users";
    }
}
