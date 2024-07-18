package com.example.PhoneManagement.controller;


import com.example.PhoneManagement.entity.Users;
import com.example.PhoneManagement.service.imp.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ProfileController {

    @Autowired
    private LoginService loginService;

    @GetMapping("/profile")
    public String viewProfile(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        Users user = loginService.findByUserName(userDetails.getUsername());
        model.addAttribute("user", user);
        return "profiletest";
    }
}
