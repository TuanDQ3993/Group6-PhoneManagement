package com.example.PhoneManagement.service.imp;

import com.example.PhoneManagement.dto.request.UserCreate;
import com.example.PhoneManagement.dto.request.UserDTO;
import com.example.PhoneManagement.dto.request.UserUpdateRequest;
import com.example.PhoneManagement.entity.Users;

import java.util.List;

public interface UserService {
    public List<UserDTO> getAllUser();
    public UserDTO CreateUser(UserCreate userRequest);
    public Users getUserById(int userId);
    public UserDTO updateUser(int id, UserUpdateRequest request);
    public void deleteUser(int id);
}
