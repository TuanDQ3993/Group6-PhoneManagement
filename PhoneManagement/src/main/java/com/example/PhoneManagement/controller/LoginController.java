package com.example.PhoneManagement.controller;

import com.example.PhoneManagement.dto.request.AuthenticationRequest;
import com.example.PhoneManagement.dto.request.RegisterRequest;
import com.example.PhoneManagement.dto.response.AuthenticationResponse;
import com.example.PhoneManagement.entity.Users;
import com.example.PhoneManagement.security.JwtService;
import com.example.PhoneManagement.service.LoginServiceImp;
import com.example.PhoneManagement.service.imp.LoginService;
import com.example.PhoneManagement.service.imp.UserService;
import com.example.PhoneManagement.util.Cart;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletResponse;

import java.util.Collection;


@Controller
@RequestMapping("/auth")
public class LoginController {

    @Autowired
    AuthenticationRequest authenticationRequest;

    @Autowired
    UserService userService;

    @Autowired
    JwtService jwtService;

    @Autowired
    LoginService loginService;

    @Autowired
    HttpServletResponse response;

    @GetMapping("/register")
    public String formRegister(Model model) {
        model.addAttribute("registerRequest", new RegisterRequest());
        return "signup";
    }

    @PostMapping("/register")
    public String registerAccount(@ModelAttribute("registerRequest") RegisterRequest request, Model model) {
        if (userService.existEmail(request.getUserName())) {
            model.addAttribute("error", "This Username already exists!");
            return "signup";
        }
        userService.createAccount(request);
        userService.activeAccount(request.getUserName(), model);
        return "signup";
    }

    @GetMapping("/active")
    public String activeAccount(@RequestParam("token") String token, Model model) {
        userService.activesuccess(token, model);
        return "login";
    }

    @GetMapping("/login")
    public String login(Model model) {
        model.addAttribute("loginRequest", new AuthenticationRequest());
        return "login";
    }

    @PostMapping("/login")
    public String checkLogin(@ModelAttribute("loginRequest") AuthenticationRequest request, Model model, HttpSession session) {
        try {
            AuthenticationResponse jwt = loginService.checklogin(request);

            Users user = loginService.findByUserName(request.getUsername());

            if (!user.isActive()) {
                model.addAttribute("error", "Your account is inactive!");
                return "login";
            }

            Cookie cookie = new Cookie("Authorization", jwt.getToken());
            cookie.setHttpOnly(true); // Bảo mật, ngăn JavaScript truy cập cookie này
            cookie.setPath("/"); // Đường dẫn của cookie
            cookie.setMaxAge(86400); // 1 ngày, tính bằng giây
            response.addCookie(cookie);

            Cart cart = new Cart();
            session.setAttribute("cart", cart);
            if (user.getRole().getRoleName().equals("ADMIN")) {
                return "redirect:/admin/dashboard";
            } else if (user.getRole().getRoleName().equals("SALER")) {
                return "redirect:/saler/dashboard";
            } else if (user.getRole().getRoleName().equals("TECHNICAL STAFF")) {
                return "redirect:/technical/dashboard_technical";
            } else {
                return "redirect:/home/homepage";
            }

        } catch (Exception e) {
            model.addAttribute("error", "Login unsuccessful. Please check the information again.");
            return "login";
        }
    }

    @GetMapping("/home")
    public String home(Model model, Authentication authentication) {
        model.addAttribute("username", authentication.getName());
        return "home";
    }

//    @GetMapping("/logout")
//    public String logout(HttpServletResponse response) {
//
//        Cookie cookie = new Cookie("Authorization", null);
//        cookie.setHttpOnly(true);
//        cookie.setPath("/");
//        cookie.setMaxAge(0);
//        response.addCookie(cookie);
//
//        SecurityContextHolder.clearContext();
//
//        return "redirect:/home/homepage";
//    }


}