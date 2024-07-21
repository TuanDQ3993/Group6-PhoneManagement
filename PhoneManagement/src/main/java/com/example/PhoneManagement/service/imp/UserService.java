package com.example.PhoneManagement.service.imp;

import com.example.PhoneManagement.dto.request.UserCreate;
import com.example.PhoneManagement.dto.request.UserDTO;
import com.example.PhoneManagement.dto.request.UserUpdateRequest;
import com.example.PhoneManagement.entity.Users;

import java.util.List;
import java.util.Optional;

public interface UserService {
    List<UserDTO> getAllUser();

    UserDTO CreateUser(UserCreate userRequest);

    Users getUserById(int userId);

    UserDTO updateUser(int id, UserUpdateRequest request);

    void deleteUser(int id);

    Optional<Users> findByEmail(String email);

    void updatePassword(Users user, String password);

}
