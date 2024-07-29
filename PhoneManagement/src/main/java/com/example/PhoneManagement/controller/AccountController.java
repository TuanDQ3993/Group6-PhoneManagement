package com.example.PhoneManagement.controller;

import com.example.PhoneManagement.dto.request.PageDTO;
import com.example.PhoneManagement.entity.Roles;
import com.example.PhoneManagement.entity.Users;
import com.example.PhoneManagement.service.RoleServiceImp;
import com.example.PhoneManagement.service.AccountServiceImp;
import org.springframework.data.domain.Pageable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.ui.Model;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;
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
    public String showDetailsUser(Model model, @PathVariable int userid) {
        Users theUser = accountServiceImp.getUserById(userid);
        List<Roles> roles = roleServiceImp.getAllRoles();
        model.addAttribute("roles", roles);
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
        model.addAttribute("size", pageSize);
        model.addAttribute("userForm", new Users());
        model.addAttribute("rolesFilter", roleServiceImp.getAllRoles());
    }

    @PostMapping("/saveAccount")
    public String saveAccount(@ModelAttribute("userForm") Users user, Model model, Authentication authentication) {
        if (accountServiceImp.isPhoneExist(user.getPhoneNumber()) || accountServiceImp.isEmailExist(user.getUsername())) {
            model.addAttribute("phoneExistsError", "Số điện thoại đã tồn tại. Vui lòng nhập số điện thoại khác.");
            model.addAttribute("emailExistsError", "Email đã tồn tại. Vui lòng nhập emails khác.");

            Users currentUser = (Users) authentication.getPrincipal();
            addUserPageAttributes(model, 0, 5, "", null, currentUser.getUserId());

            return "accountlist";

        }
        user.setCreatedAt(new Date());
        user.setActive(true);
        user.setRole(roleServiceImp.getRoleByName("USER")); // Giả sử bạn có roleService để lấy thông tin role
        user.setPassword(passwordEncoder.encode("123456")); // Giả sử bạn có passwordEncoder để mã hóa mật khẩu

        accountServiceImp.createUser(user);
        return "redirect:/admin/users";
    }





}
