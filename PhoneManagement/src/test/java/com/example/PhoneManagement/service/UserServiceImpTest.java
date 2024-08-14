package com.example.PhoneManagement.service;

import com.example.PhoneManagement.dto.request.*;
import com.example.PhoneManagement.entity.Roles;
import com.example.PhoneManagement.entity.Users;
import com.example.PhoneManagement.repository.RoleRepository;
import com.example.PhoneManagement.repository.UserRepository;
import com.example.PhoneManagement.security.JwtService;
import com.example.PhoneManagement.service.imp.EmailService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceImpTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private UserServiceImp userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetUserById_UserExists() {

        Users mockUser = new Users();
        mockUser.setUserId(1);
        mockUser.setUserName("quangtuan");
        when(userRepository.findById(1)).thenReturn(Optional.of(mockUser));
        Users user = userService.getUserById(1);
        assertNotNull(user);
        assertEquals("quangtuan", user.getUserName());
        verify(userRepository, times(1)).findById(1);
    }

    @Test
    void testGetUserById_UserDoesNotExist() {

        when(userRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> userService.getUserById(1));
        verify(userRepository, times(1)).findById(1);
    }
    @Test
    void testCreateUser() {
        // Arrange
        UserCreate userCreate = new UserCreate();
        userCreate.setUserName("quangtuan@gmail.com");
        userCreate.setPassword("123");
        userCreate.setFullName("quang tuan");
        userCreate.setAddress("123 viet nam");
        userCreate.setPhoneNumber("1234567890");
        userCreate.setRoleId(1);

        Roles mockRole = new Roles();
        mockRole.setRoleId(1);
        when(roleRepository.findById(1)).thenReturn(mockRole);

        Users savedUser = new Users();
        savedUser.setUserName("quangtuan@gmail.com");
        when(userRepository.save(any(Users.class))).thenReturn(savedUser);


        UserDTO userDTO = userService.CreateUser(userCreate);


        assertNotNull(userDTO);
        verify(userRepository, times(1)).save(any(Users.class));
        verify(roleRepository, times(1)).findById(1);
    }
    @Test
    void testChangePassword_Success() {

        ChangePasswordRequest request = new ChangePasswordRequest();
        request.setCurrentPassword("123123");
        request.setNewPassword("111111");
        request.setConfirmPassword("111111");

        Users mockUser = new Users();
        mockUser.setUserId(1);
        mockUser.setPassword("encodedOldPassword");

        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(mockUser);
        when(userRepository.findById(1)).thenReturn(Optional.of(mockUser));
        when(passwordEncoder.matches("123123", "encodedOldPassword")).thenReturn(true);
        when(passwordEncoder.encode("111111")).thenReturn("encodedNewPassword");


        userService.changePassword(request, authentication);


        verify(userRepository, times(1)).save(mockUser);
        assertEquals("encodedNewPassword", mockUser.getPassword());
    }

    @Test
    void testChangePassword_CurrentPasswordIncorrect() {

        ChangePasswordRequest request = new ChangePasswordRequest();
        request.setCurrentPassword("123123");
        request.setNewPassword("111111");
        request.setConfirmPassword("111111");

        Users mockUser = new Users();
        mockUser.setUserId(1);
        mockUser.setPassword("encodedOldPassword");

        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(mockUser);
        when(userRepository.findById(1)).thenReturn(Optional.of(mockUser));
        when(passwordEncoder.matches("123123", "encodedOldPassword")).thenReturn(false);


        assertThrows(IllegalArgumentException.class, () -> userService.changePassword(request, authentication));
        verify(userRepository, never()).save(any(Users.class));
    }

}
