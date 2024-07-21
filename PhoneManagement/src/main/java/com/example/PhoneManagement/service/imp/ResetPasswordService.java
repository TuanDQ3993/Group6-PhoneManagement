package com.example.PhoneManagement.service.imp;

import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

public interface ResetPasswordService {
    void forgotPassword(String email, Model model);

    boolean resetPassword(String token, String password, RedirectAttributes redirectAttributes);
}
