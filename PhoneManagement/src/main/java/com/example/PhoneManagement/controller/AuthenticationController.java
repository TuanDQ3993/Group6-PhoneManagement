package com.example.PhoneManagement.controller;

import com.example.PhoneManagement.dto.request.AuthenticationRequest;
import com.example.PhoneManagement.dto.request.RegisterRequest;
import com.example.PhoneManagement.dto.response.AuthenticationResponse;
import com.example.PhoneManagement.service.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {

    @Autowired
    AuthenticationService authenticationService;

//    @PostMapping("/register")
//    public ResponseEntity<AuthenticationResponse> register(@RequestBody RegisterRequest registerRequest) {
//    return ResponseEntity.ok(authenticationService.register(registerRequest));
//    }

    @PostMapping("/autenticate")
    public ResponseEntity<AuthenticationResponse> authenticate(@RequestBody AuthenticationRequest authenticationRequest) {
        return ResponseEntity.ok(authenticationService.authenticate(authenticationRequest));
    }
}
