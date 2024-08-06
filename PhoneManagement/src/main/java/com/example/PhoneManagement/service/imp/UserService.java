package com.example.PhoneManagement.service.imp;

import com.example.PhoneManagement.dto.request.*;
import com.example.PhoneManagement.entity.Users;
import org.springframework.security.core.Authentication;
import org.springframework.ui.Model;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

public interface UserService {
    List<UserDTO> getAllUser();

    UserDTO CreateUser(UserCreate userRequest);

    Users getUserById(int userId);

    UserDTO updateUser(int id, UserUpdateRequest request);

    void deleteUser(int id);

    Optional<UserDTO> getUserByUserName(String userName);

    void updateUser(UserDTO userDTO);

    void changePassword(ChangePasswordRequest request, Authentication authentication);

    Optional<Users> findByEmail(String email);

    void updatePassword(Users user, String password);

    Users getUserByName(String email);

    boolean existEmail(String email);

    void createAccount(RegisterRequest registerRequest);

    void activeAccount(String email, Model model);

    void activesuccess(String token,Model model);

}
