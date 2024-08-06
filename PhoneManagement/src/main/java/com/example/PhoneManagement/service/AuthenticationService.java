package com.example.PhoneManagement.service;

import com.example.PhoneManagement.dto.request.AuthenticationRequest;
import com.example.PhoneManagement.dto.request.RegisterRequest;
import com.example.PhoneManagement.dto.response.AuthenticationResponse;
import com.example.PhoneManagement.entity.Users;
import com.example.PhoneManagement.repository.RoleRepository;
import com.example.PhoneManagement.repository.UserRepository;
import com.example.PhoneManagement.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    JwtService jwtService;

    @Autowired
    AuthenticationManager authenticationManager;


//    public AuthenticationResponse register(RegisterRequest registerRequest) {
//        if (userRepository.existsByUserName(registerRequest.getUserName())) {
//            throw new IllegalArgumentException("Username/Email is already taken");
//        }
//        var user = Users.builder()
//                .userName(registerRequest.getUserName())
//                .password(passwordEncoder.encode(registerRequest.getPassword()))
//                .fullName(registerRequest.getFullName())
//                .address(registerRequest.getAddress())
//                .phoneNumber(registerRequest.getPhoneNumber())
//                .role(roleRepository.findById(4))
//                .active(true)
//                .createdAt(new Date())
//                .build();
//
//        userRepository.save(user);
//        var jwtToken = jwtService.generateToken(user);
//        return new AuthenticationResponse(jwtToken);
//    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken
                        (request.getUsername(), request.getPassword()
                        ));
        var user = userRepository.findByUserName(request.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        var jwtToken = jwtService.generateToken(user);
        return new AuthenticationResponse(jwtToken);
    }
}