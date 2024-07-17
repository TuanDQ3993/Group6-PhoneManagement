package com.example.PhoneManagement.service;

import com.example.PhoneManagement.dto.request.UserDTO;
import org.springframework.stereotype.Service;
import java.util.Optional;


public interface UserService {
    Optional<UserDTO> getUserByUserName(String userName);
    void updateUser(UserDTO userDTO);
}
