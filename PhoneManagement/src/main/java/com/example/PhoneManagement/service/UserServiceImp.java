package com.example.PhoneManagement.service.imp;

import com.example.PhoneManagement.dto.request.UserCreate;
import com.example.PhoneManagement.dto.request.UserDTO;
import com.example.PhoneManagement.dto.request.UserUpdateRequest;
import com.example.PhoneManagement.entity.Roles;
import com.example.PhoneManagement.entity.Users;
import com.example.PhoneManagement.repository.RoleRepository;
import com.example.PhoneManagement.repository.UserRepository;
import com.example.PhoneManagement.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImp implements UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;
    public List<UserDTO> getAllUser() {
        List<Users> users = userRepository.findAll();
        List<UserDTO> userDTOList = new ArrayList<>();
        for (Users user : users) {
            UserDTO userDTO = new UserDTO();
            userDTO.setUserName(user.getUserName());
            userDTO.setFullName(user.getFullName());
            userDTO.setAddress(user.getAddress());
            userDTO.setPhoneNumber(user.getPhoneNumber());
            userDTOList.add(userDTO);
        }
        return userDTOList;
    }

    public UserDTO CreateUser(UserCreate userRequest) {
        Users user = new Users();
        user.setUserName(userRequest.getUserName());
        user.setPassword(userRequest.getPassword());
        user.setFullName(userRequest.getFullName());
        user.setAddress(userRequest.getAddress());
        user.setPhoneNumber(userRequest.getPhoneNumber());
        user.setCreatedAt(new Date());
        Roles role=roleRepository.findById(userRequest.getRoleId());
        user.setRole(role);
        userRepository.save(user);
        UserDTO userDTO = new UserDTO();

        return userDTO;
    }

    public Users getUserById(int userId) {
        return userRepository.findById(userId).orElseThrow(()->new RuntimeException("User Not Found"));
    }

    public UserDTO updateUser(int id, UserUpdateRequest request) {
        Users user = getUserById(id);
        user.setUserName(request.getUserName());
        user.setPassword(request.getPassword());
        user.setFullName(request.getFullName());
        user.setAddress(request.getAddress());
        user.setPhoneNumber(request.getPhoneNumber());
        userRepository.save(user);
        UserDTO userDTO = new UserDTO(user.getUserId(),user.getUserName(),user.getPassword(),user.getFullName()
                ,user.getAddress(),user.getPhoneNumber(),user.getCreatedAt(),user.getRole().getRoleId());

        return userDTO;

    }

    public void deleteUser(int id) {
        userRepository.deleteById(id);
    }

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
