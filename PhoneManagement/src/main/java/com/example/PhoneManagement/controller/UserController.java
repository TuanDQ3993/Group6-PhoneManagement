package com.example.PhoneManagement.controller;

import com.example.PhoneManagement.dto.request.UserDTO;
import com.example.PhoneManagement.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;
import java.util.Optional;

@RestController
@RequestMapping ("/user")
public class UserController {

    @Autowired
    private UserService userService;

    /* Test treen Postman
    @GetMapping("/profile")
    public UserDTO getProfile(Model model, @RequestBody LoginRequest loginRequest) {
        String userName = loginRequest.getUsername();
        Optional<UserDTO> userDTO = userService.getUserByUserName(userName);
        if(userDTO.isPresent()) {
            model.addAttribute("user", userDTO.get());
            return userDTO.get();
        }else {
            return new UserDTO();
        }
    }
     */

    @GetMapping("/profile")
    public UserDTO getProfile(Model model, Principal principal) {
        String userName = principal.getName();
        Optional<UserDTO> userDTO = userService.getUserByUserName(userName);
        if(userDTO.isPresent()) {
            model.addAttribute("user", userDTO.get());
            return userDTO.get();
        }else {
            return new UserDTO();
        }
    }

    @PutMapping("/profile")
    public String updateProfile(@ModelAttribute("user") UserDTO userDTO) {
        userService.updateUser(userDTO);
        return "redirect:/profile?success";
    }


}
