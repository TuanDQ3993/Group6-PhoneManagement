package com.example.PhoneManagement.controller;

import com.example.PhoneManagement.dto.request.UserDTO;
import com.example.PhoneManagement.dto.request.UserUpdateRequest;
import com.example.PhoneManagement.entity.Users;
import com.example.PhoneManagement.entity.request.LoginRequest;
import com.example.PhoneManagement.service.imp.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;
import java.util.Optional;

@RestController
@RequestMapping ("/user")
public class UserController {

    @Autowired
    private UserService userService;

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
        if(userDTO.isPresent()) {
            model.addAttribute("user", userDTO.get());
            return "profile";
        }else {
            return "login";
        }
    }

   @PutMapping("/profile")
    public UserDTO updateProfile(@Valid @RequestBody UserDTO userDTO, BindingResult bindingResult, Model model) {
        if(bindingResult.hasErrors()) {
            model.addAttribute("errors", bindingResult.getAllErrors());
            return userDTO;
        }
        userService.updateUser(userDTO);
        model.addAttribute("success", "Update Profile Success");
        return userDTO;
   }

    @PutMapping("/{userid}")
    public ResponseEntity<?> updateUser(@PathVariable int userid, @RequestBody UserUpdateRequest request) {
        return new ResponseEntity<>(userService.updateUser(userid,request), HttpStatus.OK);
    }

    @DeleteMapping("/{userid}")
    public String deleteUser(@PathVariable int userid) {
        userService.deleteUser(userid);
        return "User deleted";
    }
}
