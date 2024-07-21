package com.example.PhoneManagement.controller;

import com.example.PhoneManagement.entity.Users;
import com.example.PhoneManagement.security.JwtService;
import com.example.PhoneManagement.service.imp.ResetPasswordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


import java.util.Optional;

@Controller
@RequestMapping("/password")
public class ResetPasswordController {
    @Autowired
    ResetPasswordService resetPasswordService;

    @Autowired
    JwtService jwtService;

    @GetMapping("/forgot")
    public String forgot() {
        return "forgot_password";
    }

    @PostMapping("/forgot")
    public String forgotPassword(@RequestParam("email") String email, Model model) {
        resetPasswordService.forgotPassword(email, model);
        return "forgot_password";
    }

    @GetMapping("/reset")
    public String reset(@RequestParam("token") String token, Model model) {
        String username = jwtService.extractUsername(token);
        if (username != null && !jwtService.isTokenExpired(token)) {
            model.addAttribute("token", token);
            return "reset_password";
        } else {
            model.addAttribute("error", "Invalid or expired password reset token.");
            return "forgot_password";
        }
    }

    @PostMapping("/reset")
    public String resetPassword(@RequestParam("token") String token, @RequestParam("password") String password, RedirectAttributes redirectAttributes) {
        boolean success = resetPasswordService.resetPassword(token, password, redirectAttributes);
        if (success) {
            return "redirect:/auth/login";
        } else {
            return "forgot_password";
        }
    }
}
