package com.example.PhoneManagement.controller;

import com.example.PhoneManagement.dto.request.PageDTO;
import com.example.PhoneManagement.entity.Roles;
import com.example.PhoneManagement.entity.Users;
import com.example.PhoneManagement.service.RoleServiceImp;
import com.example.PhoneManagement.service.AccountServiceImp;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
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

    //Show all user with paging
    @GetMapping("/users")
    public String getAllUser(Model model, @RequestParam(defaultValue = "0") int page,
                             @RequestParam(defaultValue = "5") int pageRecord) {
        PageDTO pageDTO = new PageDTO();
        pageDTO.setPageNumber(page);
        pageDTO.setPageSize(pageRecord);

        Page<Users> userPage = accountServiceImp.findPaginated(pageDTO);
        model.addAttribute("userPage", userPage);
//        int userSessionID = 10;
//        model.addAttribute("userSession", userSessionID);
        int totalPages = userPage.getTotalPages();
        if (totalPages > 0) {
            List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPages).boxed().collect(Collectors.toList());
            model.addAttribute("pageNumbers", pageNumbers);
        }
        model.addAttribute("size", pageRecord);
        return "accountlist";
    }


    // View details each user
    @GetMapping("/viewUser/{userid}")
    public String showDetailsUser(Model model, @PathVariable int userid) {
        Users theUser = accountServiceImp.getUserById(userid);
        List<Roles> roles = roleServiceImp.getAllRoles();
        model.addAttribute("roles", roles);
        model.addAttribute("users", theUser);
        return "users";
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

    // Search user by their full name
    @GetMapping("/searchUser")
    public String searchUser(@RequestParam("query") String text, Model model) {
        List<Users> searchResults = accountServiceImp.searchUsers(text);
        model.addAttribute("userPage", searchResults);
        return "accountlist";
    }

    // Change role of account
    @PostMapping("/changeRole/{id}")
    public String changeUserRole(@PathVariable int id, @RequestParam int roleId) {
        roleServiceImp.changeUserRole(id, roleId);
        return ("redirect:/admin/users");
    }

    @GetMapping("/export/excel")
    public void exportToExcel(HttpServletResponse response) throws IOException {
        response.setContentType("application/octet-stream");
        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
        String currentDateTime = dateFormatter.format(new Date());

        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=Account_List-" + currentDateTime + ".xlsx";
        response.setHeader(headerKey, headerValue);

        List<Users> listUsers = accountServiceImp.getAll();

        AccountExcelController excelExporter = new AccountExcelController(listUsers);

        excelExporter.export(response);
    }


}
