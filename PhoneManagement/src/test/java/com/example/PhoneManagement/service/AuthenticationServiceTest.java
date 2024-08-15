package com.example.PhoneManagement.service;

import com.example.PhoneManagement.dto.request.AuthenticationRequest;
import com.example.PhoneManagement.dto.request.RegisterRequest;
import com.example.PhoneManagement.dto.response.AuthenticationResponse;
import com.example.PhoneManagement.entity.Users;
import com.example.PhoneManagement.repository.RoleRepository;
import com.example.PhoneManagement.repository.UserRepository;
import com.example.PhoneManagement.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class AuthenticationServiceTest {

    @InjectMocks
    private AuthenticationService authenticationService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private AuthenticationManager authenticationManager;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }



    @Test
    void testAuthenticateSuccess() {
        AuthenticationRequest authRequest = new AuthenticationRequest();
        authRequest.setUsername("testuser");
        authRequest.setPassword("password");

        Users user = Users.builder()
                .userName("testuser")
                .password("encodedpassword")
                .build();

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(new UsernamePasswordAuthenticationToken("testuser", "encodedpassword"));
        when(userRepository.findByUserName(anyString())).thenReturn(java.util.Optional.of(user));
        when(jwtService.generateToken(any(Users.class))).thenReturn("jwtToken");

        AuthenticationResponse response = authenticationService.authenticate(authRequest);
        assertNotNull(response);
        assertEquals("jwtToken", response.getToken());
    }

    @Test
    void testAuthenticateUserNotFound() {
        AuthenticationRequest authRequest = new AuthenticationRequest();
        authRequest.setUsername("testuser");
        authRequest.setPassword("password");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(new UsernamePasswordAuthenticationToken("testuser", "password"));
        when(userRepository.findByUserName(anyString())).thenReturn(java.util.Optional.empty());

        RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
            authenticationService.authenticate(authRequest);
        });
        assertEquals("User not found", thrown.getMessage());
    }
}
