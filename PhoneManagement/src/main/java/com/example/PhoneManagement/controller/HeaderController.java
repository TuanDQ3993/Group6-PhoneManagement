package com.example.PhoneManagement.controller;

import com.example.PhoneManagement.dto.request.UserDTO;
import com.example.PhoneManagement.service.imp.UserService;
import com.example.PhoneManagement.util.Cart;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.security.Principal;
import java.util.Optional;

@ControllerAdvice
public class HeaderController {

    @Autowired
    private UserService userService;

    @ModelAttribute
    public void addAttributes(Principal principal, HttpSession session, Model model) {
        // Get user
        if (principal != null) {
            String userName = principal.getName();
            Optional<UserDTO> userDTO = userService.getUserByUserName(userName);
            userDTO.ifPresent(user -> model.addAttribute("user", user));
        }

        // Get cart
        Cart cart = (Cart) session.getAttribute("cart");
        if (cart != null) {
            model.addAttribute("cart", cart);
            model.addAttribute("size", cart.getItems().size());
            model.addAttribute("total", cart.getTotalPrice());
        } else {
            model.addAttribute("size", 0);
            model.addAttribute("total", 0.0);
        }
    }
}
