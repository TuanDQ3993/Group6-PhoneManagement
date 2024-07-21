package com.example.PhoneManagement.controller;

import com.example.PhoneManagement.dto.request.ChangePasswordRequest;
import com.example.PhoneManagement.dto.request.UserDTO;
import com.example.PhoneManagement.dto.request.UserUpdateRequest;
import com.example.PhoneManagement.service.imp.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;
import java.util.Optional;

@Controller
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

   @PostMapping("/profile")
   public String updateProfile(@Valid UserDTO userDTO, BindingResult bindingResult, Model model) {
       if (bindingResult.hasErrors()) {
           model.addAttribute("errors", bindingResult.getAllErrors());
           return "profile";
       }
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
   public String changePassword( ChangePasswordRequest request, BindingResult bindingResult, Authentication authentication, Model model){
        if(bindingResult.hasErrors()){
            return "change_password";
        }
        try{
            userService.changePassword(request,authentication);
            model.addAttribute("success", "Change Password Success");
        }catch (IllegalArgumentException e){
            model.addAttribute("errors", e.getMessage());
        }
        return "change_password";
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
