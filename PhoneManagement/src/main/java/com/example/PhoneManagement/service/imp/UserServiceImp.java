package com.example.PhoneManagement.service.imp;

import com.example.PhoneManagement.dto.request.UserDTO;
import com.example.PhoneManagement.entity.Users;
import com.example.PhoneManagement.repository.UserRepository;
import com.example.PhoneManagement.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserServiceImp implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public Optional<UserDTO> getUserByUserName(String userName) {
        Optional<Users> userOpt = userRepository.findByUserName(userName);
        if(userOpt.isPresent()){
            Users user = userOpt.get(); //nếu userOpt trùng với csdl có trong Users thì gán vào user
            UserDTO userDTO = new UserDTO();
            userDTO.setUserName(user.getUserName());
            userDTO.setFullName(user.getFullName());
            userDTO.setAddress(user.getAddress());
            userDTO.setPhoneNumber(user.getPhoneNumber());
            userDTO.setAvatar(user.getAvatar());
            return Optional.of(userDTO);
        }
        return Optional.empty();
    }

    @Override
    public void updateUser(UserDTO userDTO) {
        Optional<Users> userOpt = userRepository.findByUserName(userDTO.getUserName());
        if(userOpt.isPresent()){
            Users user = userOpt.get();
            user.setFullName(userDTO.getFullName());
            user.setAddress(userDTO.getAddress());
            user.setPhoneNumber(userDTO.getPhoneNumber());
            user.setAvatar(userDTO.getAvatar());
            userRepository.save(user);
        }
    }
}
