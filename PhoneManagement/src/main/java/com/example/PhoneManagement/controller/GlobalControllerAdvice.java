package com.example.PhoneManagement.controller;

import com.example.PhoneManagement.entity.Users;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class GlobalControllerAdvice {

    @ModelAttribute
    public void getLoggedUser(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            Object principal = authentication.getPrincipal();
            if (principal instanceof Users) {
                Users loggedUser = (Users) principal;
                String roleName = loggedUser.getRole().getRoleName();
                model.addAttribute("fullname", loggedUser.getFullName());
                model.addAttribute("roleLogged", roleName);
                model.addAttribute("avatar", loggedUser.getAvatar());

                // Log thông tin người dùng và vai trò
                System.out.println("Logged user: " + loggedUser.getFullName() + ", Role: " + roleName);
            }
        }
    }
}


