package com.example.PhoneManagement.controller;

import com.example.PhoneManagement.dto.request.UserCreate;
import com.example.PhoneManagement.dto.request.UserUpdateRequest;
import com.example.PhoneManagement.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping ("/user")
public class UserController {

    @Autowired
    private UserService userService;
    @GetMapping("")
    public ResponseEntity<?> getAllUser() {
        return new ResponseEntity<>(userService.getAllUser(), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<?> addUser(@RequestBody UserCreate request) {
        return new ResponseEntity<>(userService.CreateUser(request), HttpStatus.OK);
    }

    @GetMapping("/{userid}")
    public ResponseEntity<?> getUserById(@PathVariable int userid) {
        return new ResponseEntity<>(userService.getUserById(userid), HttpStatus.OK);
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
