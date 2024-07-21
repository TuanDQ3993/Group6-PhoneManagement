package com.example.PhoneManagement.service;

import com.example.PhoneManagement.entity.Users;
import com.example.PhoneManagement.security.JwtService;
import com.example.PhoneManagement.service.imp.EmailService;
import com.example.PhoneManagement.service.imp.ResetPasswordService;
import com.example.PhoneManagement.service.imp.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Service
public class ResetPasswordServiceImp implements ResetPasswordService {

    @Autowired
    private UserService userService;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private EmailService emailService;

    @Override
    public void forgotPassword(String email, Model model) {
        Optional<Users> userOptional = userService.findByEmail(email);
        if (userOptional.isPresent()) {
            Users user = userOptional.get();
            UserDetails userDetails = userDetailsService.loadUserByUsername(user.getUserName());
            String token = jwtService.generatePasswordResetToken(userDetails);
            String resetLink = "http://localhost:8080/password/reset?token=" + token;
            emailService.sendEmail(user.getUserName(), "Password Reset Request", "To reset your password, click the link below:\n" + resetLink);
            model.addAttribute("message", "Password reset link has been sent to your email.");
        } else {
            model.addAttribute("error", "No account found with that email address.");
        }
    }

    @Override
    public boolean resetPassword(String token, String password, RedirectAttributes redirectAttributes) {
        String username = jwtService.extractUsername(token);
        if (username != null && !jwtService.isTokenExpired(token)) {
            Users user = userService.findByEmail(username).orElseThrow();
            userService.updatePassword(user, password);
            redirectAttributes.addFlashAttribute("message", "Password has been reset successfully.");
            return true;
        } else {
            redirectAttributes.addFlashAttribute("error", "Invalid or expired password reset token.");
            return false;
        }
    }
}
