package com.example.PhoneManagement.controller;

import com.example.PhoneManagement.dto.request.ChangePasswordRequest;
import com.example.PhoneManagement.dto.request.UserDTO;
import com.example.PhoneManagement.dto.request.UserUpdateRequest;
import com.example.PhoneManagement.service.imp.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.Optional;

@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private HttpServletRequest httpServletRequest;
    @Autowired
    private HttpServletResponse httpServletResponse;

//    // Test treen Postman
//    @GetMapping("/profile")
//    public UserDTO getProfile(Model model, @RequestBody LoginRequest loginRequest) {
//        String userName = loginRequest.getUserName();
//        Optional<UserDTO> userDTO = userService.getUserByUserName(userName);
//        if(userDTO.isPresent()) {
//            model.addAttribute("user", userDTO.get());
//            return userDTO.get();
//        }else {
//            return new UserDTO();
//        }
//    }


    @GetMapping("/profile")
    public String getProfile(Model model, Principal principal) {
        String userName = principal.getName();
        Optional<UserDTO> userDTO = userService.getUserByUserName(userName);
        if (userDTO.isPresent()) {
            model.addAttribute("user", userDTO.get());
            return "profile";
        } else {
            return "login";
        }
    }

    @PostMapping("/profile")
    public String updateProfile(@Valid UserDTO userDTO, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("errors", bindingResult.getAllErrors());
            return "profile";
        }

        // Định nghĩa đường dẫn lưu tệp
        Path uploadDir = Paths.get("uploads/");

        // Kiểm tra và tạo thư mục nếu không tồn tại
        if (!Files.exists(uploadDir)) {
            try {
                Files.createDirectories(uploadDir);
            } catch (IOException e) {
                e.printStackTrace();
                model.addAttribute("errors", "Failed to create upload directory");
                return "profile";
            }
        }

        // Xử lý tệp tải lên
        if (userDTO.getAvatarFile() != null && !userDTO.getAvatarFile().isEmpty()) {
            try {
                // Đặt tên tệp và đường dẫn đầy đủ
                String fileName = userDTO.getAvatarFile().getOriginalFilename();
                Path filePath = uploadDir.resolve(fileName);

                // Sao chép tệp vào thư mục lưu trữ
                try (InputStream inputStream = userDTO.getAvatarFile().getInputStream()) {
                    Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
                }

                // Cập nhật đường dẫn ảnh trong DTO
                userDTO.setAvatar("/uploads/" + fileName.toLowerCase());
            } catch (IOException e) {
                e.printStackTrace();
                model.addAttribute("errors", "Failed to upload image");
                return "profile";
            }
        }

        // Cập nhật thông tin người dùng
        userService.updateUser(userDTO);
        model.addAttribute("user", userDTO);
        model.addAttribute("success", "Update Profile Success");
        return "profile";
    }


    @GetMapping("/change_password")
    public String showChangePasswordForm(Model model) {
        model.addAttribute("changePasswordRequest", new ChangePasswordRequest());
        return "change_password";
    }

    @PostMapping("/change_password")
    public String changePassword(ChangePasswordRequest request, BindingResult bindingResult, Authentication authentication, HttpServletRequest req, HttpServletResponse res, Model model, RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            return "change_password";
        }
        try {
            userService.changePassword(request, authentication);

            // Thêm thông báo vào RedirectAttributes
            redirectAttributes.addFlashAttribute("message", "Change password successful! Please login");

            // Thực hiện logout thủ công
            new SecurityContextLogoutHandler().logout(req, res, authentication);

            // Xóa tất cả cookie liên quan
            Cookie[] cookies = req.getCookies();
            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    if (cookie.getName().equals("JSESSIONID") || cookie.getName().equals("Authorization")) {
                        cookie.setPath("/");
                        cookie.setMaxAge(0);
                        res.addCookie(cookie);
                    }
                }
            }

            // Thiết lập lại SecurityContext
            SecurityContextHolder.clearContext();

            // Chuyển hướng đến trang đăng nhập
            return "redirect:/auth/login";

        } catch (IllegalArgumentException e) {
            model.addAttribute("errors", e.getMessage());
            return "change_password";
        }
    }


    @PutMapping("/{userid}")
    public ResponseEntity<?> updateUser(@PathVariable int userid, @RequestBody UserUpdateRequest request) {
        return new ResponseEntity<>(userService.updateUser(userid, request), HttpStatus.OK);
    }

    @DeleteMapping("/{userid}")
    public String deleteUser(@PathVariable int userid) {
        userService.deleteUser(userid);
        return "User deleted";
    }

}
