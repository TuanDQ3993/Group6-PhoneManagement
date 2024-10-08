package com.example.PhoneManagement.service;

import com.example.PhoneManagement.dto.request.*;
import com.example.PhoneManagement.entity.Roles;
import com.example.PhoneManagement.entity.Users;
import com.example.PhoneManagement.repository.RoleRepository;
import com.example.PhoneManagement.repository.UserRepository;
import com.example.PhoneManagement.security.JwtService;
import com.example.PhoneManagement.service.imp.EmailService;
import com.example.PhoneManagement.service.imp.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class UserServiceImp implements UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private EmailService emailService;

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
        Roles role = roleRepository.findById(userRequest.getRoleId());
        user.setRole(role);
        userRepository.save(user);
        UserDTO userDTO = new UserDTO();

        return userDTO;
    }

    public Users getUserById(int userId) {
        return userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User Not Found"));
    }

    public UserDTO updateUser(int id, UserUpdateRequest request) {
        Users user = getUserById(id);
        user.setUserName(request.getUserName());
        user.setPassword(request.getPassword());
        user.setFullName(request.getFullName());
        user.setAddress(request.getAddress());
        user.setPhoneNumber(request.getPhoneNumber());
        userRepository.save(user);
        UserDTO userDTO = new UserDTO();

        return userDTO;

    }

    public void deleteUser(int id) {
        userRepository.deleteById(id);
    }

    @Override
    public Optional<UserDTO> getUserByUserName(String userName) {
        Optional<Users> userOpt = userRepository.findByUserName(userName);
        if (userOpt.isPresent()) {
            Users user = userOpt.get(); //nếu userOpt trùng với csdl có trong Users thì gán vào user
            UserDTO userDTO = new UserDTO();
            userDTO.setUserId(user.getUserId());
            userDTO.setUserName(user.getUserName());
            userDTO.setFullName(user.getFullName());
            userDTO.setAddress(user.getAddress());
            userDTO.setPhoneNumber(user.getPhoneNumber());
            userDTO.setAvatar(user.getAvatar());
            userDTO.setRoleName(user.getRole().getRoleName());
            return Optional.of(userDTO);
        }
        return Optional.empty();
    }

    @Override
    public void updateUser(UserDTO userDTO) {
        Optional<Users> userOpt = userRepository.findByUserName(userDTO.getUserName());
        if (userOpt.isPresent()) {
            Users user = userOpt.get();
            user.setFullName(userDTO.getFullName());
            user.setAddress(userDTO.getAddress());
            user.setPhoneNumber(userDTO.getPhoneNumber());
            user.setAvatar(userDTO.getAvatar());
            Roles role = roleRepository.findByRoleName(userDTO.getRoleName());
            user.setRole(role);
            userRepository.save(user);
        }
    }

    public void changePassword(ChangePasswordRequest request, Authentication authentication) {
        Users user = (Users) authentication.getPrincipal();
        Users users = userRepository.findById(user.getUserId()).orElseThrow(() -> new RuntimeException("User Not Found"));
        // check password
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Current password is incorrect.");
        }
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new IllegalArgumentException("New password and confirmation do not match.");
        }
        users.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(users);
    }


    @Override
    public Optional<Users> findByEmail(String email) {
        return userRepository.findByUserName(email);
    }

    @Override
    public void updatePassword(Users user, String password) {
        user.setPassword(passwordEncoder.encode(password));
        userRepository.save(user);
    }

    @Override
    public Users getUserByName(String email) {
        return userRepository.findByUserName(email).get();
    }

    @Override
    public boolean existEmail(String email) {
        return userRepository.existsByUserName(email);
    }

    @Override
    public void createAccount(RegisterRequest registerRequest) {
        var user = Users.builder()
                .userName(registerRequest.getUserName())
                .password(passwordEncoder.encode(registerRequest.getPassword()))
                .fullName(registerRequest.getFullName())
                .address(registerRequest.getAddress())
                .phoneNumber(registerRequest.getPhoneNumber())
                .role(roleRepository.findById(4))
                .active(false)
                .createdAt(new Date())
                .build();

        userRepository.save(user);
    }

    @Override
    public void activeAccount(String email, Model model) {
        Optional<Users> userOptional = findByEmail(email);
        if (userOptional.isPresent()) {
            Users user = userOptional.get();
            UserDetails userDetails = userDetailsService.loadUserByUsername(user.getUserName());
            String token = jwtService.generateActiveAccountToken(userDetails);
            String resetLink = "http://localhost:8080/auth/active?token=" + token;
            emailService.sendEmail(user.getUserName(), "Active Account Request", "To active your account, click the link below:\n" + resetLink);
            model.addAttribute("message", "Go to your email to activate your account.");
        } else {
            model.addAttribute("error", "Please register your account again.");
        }
    }

    @Override
    public void activesuccess(String token, Model model) {
        String username = jwtService.extractUsername(token);
        if (username != null && !jwtService.isTokenExpired(token)) {
            Users user = findByEmail(username).orElseThrow();
            user.setActive(true);
            userRepository.save(user);
            model.addAttribute("message", "Your account has been activated.");
        } else {
            model.addAttribute("error", "Invalid or expired password reset token.");
        }
    }
}
