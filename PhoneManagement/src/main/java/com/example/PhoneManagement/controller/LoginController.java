package com.example.PhoneManagement.controller;

import com.example.PhoneManagement.dto.request.AuthenticationRequest;
import com.example.PhoneManagement.dto.response.AuthenticationResponse;
import com.example.PhoneManagement.entity.Users;
import com.example.PhoneManagement.security.JwtService;
import com.example.PhoneManagement.service.LoginServiceImp;
import com.example.PhoneManagement.service.imp.LoginService;
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
    JwtService jwtService;

    @Autowired
    LoginService loginService;

    @Autowired
    HttpServletResponse response;


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
                model.addAttribute("error", "Your account is inactive.Login other account!");
                return "login";
            }

            Cookie cookie = new Cookie("Authorization", jwt.getToken());
            cookie.setHttpOnly(true); // Bảo mật, ngăn JavaScript truy cập cookie này
            cookie.setPath("/"); // Đường dẫn của cookie
            cookie.setMaxAge(86400);// 1 day
            response.addCookie(cookie);

            if (user.getRole().getRoleName().equals("ADMIN")) {
                return "redirect:/auth/home";
            }
            else if(user.getRole().getRoleName().equals("SALER")) {
                return "redirect:/saler/orders";
            }
            return "login";
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

    @GetMapping("/logout")
    public String logout(HttpServletResponse response) {

        Cookie cookie = new Cookie("Authorization", null);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        response.addCookie(cookie);

        SecurityContextHolder.clearContext();

        return "redirect:/auth/login?logout";
    }


}
