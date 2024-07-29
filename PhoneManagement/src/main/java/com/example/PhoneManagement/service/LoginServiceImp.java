package com.example.PhoneManagement.service;

import com.example.PhoneManagement.dto.request.AuthenticationRequest;
import com.example.PhoneManagement.dto.response.AuthenticationResponse;
import com.example.PhoneManagement.entity.Users;
import com.example.PhoneManagement.repository.UserRepository;
import com.example.PhoneManagement.security.JwtService;
import com.example.PhoneManagement.service.imp.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class LoginServiceImp implements LoginService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    JwtService jwtService;

    @Autowired
    AuthenticationManager authenticationManager;


    @Override
    public Users findByUserName(String username) {
        return userRepository.findByUserName(username).orElseThrow(() -> new UsernameNotFoundException("user not found"));
    }

    @Override
    public AuthenticationResponse checklogin(AuthenticationRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        Users user = findByUserName(request.getUsername());
        String jwt = jwtService.generateToken(user);
        return new AuthenticationResponse(jwt);
    }
}
