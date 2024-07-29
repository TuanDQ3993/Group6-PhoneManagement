package com.example.PhoneManagement.service.imp;

import com.example.PhoneManagement.dto.request.AuthenticationRequest;
import com.example.PhoneManagement.dto.response.AuthenticationResponse;
import com.example.PhoneManagement.entity.Users;

public interface LoginService {

    Users findByUserName(String username);

    AuthenticationResponse checklogin(AuthenticationRequest request);
}
