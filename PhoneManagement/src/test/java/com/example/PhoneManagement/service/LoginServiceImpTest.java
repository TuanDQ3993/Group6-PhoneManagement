package com.example.PhoneManagement.service;

import com.example.PhoneManagement.dto.request.AuthenticationRequest;
import com.example.PhoneManagement.dto.response.AuthenticationResponse;
import com.example.PhoneManagement.entity.Users;
import com.example.PhoneManagement.repository.UserRepository;
import com.example.PhoneManagement.security.JwtService;
import com.example.PhoneManagement.service.imp.LoginService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class LoginServiceImpTest {

    @InjectMocks
    private LoginServiceImp loginService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtService jwtService;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private Authentication authentication;

    private Users user;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        user = new Users();
        user.setUserId(1);
        user.setUserName("testUser");
        user.setPassword("testPassword");
    }

    @Test
    void testFindByUserName_UserExists() {

        when(userRepository.findByUserName("testUser")).thenReturn(Optional.of(user));

        Users result = loginService.findByUserName("testUser");


        assertNotNull(result);
        assertEquals("testUser", result.getUserName());
        verify(userRepository).findByUserName("testUser");
    }

    @Test
    void testFindByUserName_UserDoesNotExist() {

        when(userRepository.findByUserName("invalidUser")).thenReturn(Optional.empty());


        assertThrows(UsernameNotFoundException.class, () -> {
            loginService.findByUserName("invalidUser");
        });
    }

    @Test
    void testCheckLogin_Success() {

        AuthenticationRequest request = new AuthenticationRequest();
        request.setUsername("testUser");
        request.setPassword("testPassword");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);
        when(userRepository.findByUserName("testUser")).thenReturn(Optional.of(user));
        when(jwtService.generateToken(user)).thenReturn("mockJwtToken");


        AuthenticationResponse response = loginService.checklogin(request);


        assertNotNull(response);
        assertEquals("mockJwtToken", response.getToken());
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(userRepository).findByUserName("testUser");
    }

    @Test
    void testCheckLogin_InvalidCredentials() {

        AuthenticationRequest request = new AuthenticationRequest();
        request.setUsername("testUser");
        request.setPassword("wrongPassword");

        doThrow(new RuntimeException("Bad credentials")).when(authenticationManager)
                .authenticate(any(UsernamePasswordAuthenticationToken.class));

        assertThrows(RuntimeException.class, () -> {
            loginService.checklogin(request);
        });

        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(userRepository, never()).findByUserName("testUser");
    }
}
