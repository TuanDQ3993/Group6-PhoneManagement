package com.example.PhoneManagement.service;

import com.example.PhoneManagement.entity.Users;
import com.example.PhoneManagement.security.JwtService;
import com.example.PhoneManagement.service.imp.EmailService;
import com.example.PhoneManagement.service.imp.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ResetPasswordServiceImpTest {

    @Mock
    private UserService userService;

    @Mock
    private UserDetailsService userDetailsService;

    @Mock
    private JwtService jwtService;

    @Mock
    private EmailService emailService;

    @Mock
    private Model model;

    @Mock
    private RedirectAttributes redirectAttributes;

    @InjectMocks
    private ResetPasswordServiceImp resetPasswordServiceImp;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testForgotPassword_UserFound() {
        Users user = new Users();
        user.setUserName("quangtuan3903@gmail.com");

        when(userService.findByEmail("quangtuan3903@gmail.com")).thenReturn(Optional.of(user));
        UserDetails userDetails = mock(UserDetails.class);
        when(userDetailsService.loadUserByUsername("quangtuan3903@gmail.com")).thenReturn(userDetails);
        when(jwtService.generatePasswordResetToken(userDetails)).thenReturn("token");

        resetPasswordServiceImp.forgotPassword("quangtuan3903@gmail.com", model);

        verify(emailService, times(1)).sendEmail(eq("quangtuan3903@gmail.com"), eq("Password Reset Request"), anyString());
        verify(model, times(1)).addAttribute(eq("message"), eq("Password reset link has been sent to your email."));
    }

    @Test
    void testForgotPassword_UserNotFound() {
        when(userService.findByEmail("quangtuan3903@gmail.com")).thenReturn(Optional.empty());

        resetPasswordServiceImp.forgotPassword("quangtuan3903@gmail.com", model);

        verify(emailService, never()).sendEmail(anyString(), anyString(), anyString());
        verify(model, times(1)).addAttribute(eq("error"), eq("No account found with that email address."));
    }

    @Test
    void testResetPassword_Success() {
        Users user = new Users();
        when(jwtService.extractUsername("token")).thenReturn("quangtuan3903@gmail.com");
        when(jwtService.isTokenExpired("token")).thenReturn(false);
        when(userService.findByEmail("quangtuan3903@gmail.com")).thenReturn(Optional.of(user));

        boolean result = resetPasswordServiceImp.resetPassword("token", "newPassword", redirectAttributes);

        assertTrue(result);
        verify(userService, times(1)).updatePassword(user, "newPassword");
        verify(redirectAttributes, times(1)).addFlashAttribute(eq("message"), eq("Password has been reset successfully."));
    }

    @Test
    void testResetPassword_TokenExpired() {
        when(jwtService.extractUsername("token")).thenReturn("quangtuan3903@gmail.com");
        when(jwtService.isTokenExpired("token")).thenReturn(true);

        boolean result = resetPasswordServiceImp.resetPassword("token", "newPassword", redirectAttributes);

        assertFalse(result);
        verify(userService, never()).updatePassword(any(), anyString());
        verify(redirectAttributes, times(1)).addFlashAttribute(eq("error"), eq("Invalid or expired password reset token."));
    }

    @Test
    void testResetPassword_InvalidToken() {
        when(jwtService.extractUsername("invalid_token")).thenReturn(null);

        boolean result = resetPasswordServiceImp.resetPassword("invalid_token", "newPassword", redirectAttributes);

        assertFalse(result);
        verify(userService, never()).updatePassword(any(), anyString());
        verify(redirectAttributes, times(1)).addFlashAttribute(eq("error"), eq("Invalid or expired password reset token."));
    }
}
