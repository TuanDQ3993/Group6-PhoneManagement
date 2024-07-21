package com.example.PhoneManagement.service.imp;

import com.example.PhoneManagement.dto.request.ChangePasswordRequest;
import com.example.PhoneManagement.dto.request.UserCreate;
import com.example.PhoneManagement.dto.request.UserDTO;
import com.example.PhoneManagement.dto.request.UserUpdateRequest;
import com.example.PhoneManagement.entity.Users;
import org.springframework.security.core.Authentication;

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

}
