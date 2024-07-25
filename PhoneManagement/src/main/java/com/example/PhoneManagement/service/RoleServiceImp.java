package com.example.PhoneManagement.service;

import com.example.PhoneManagement.entity.Roles;
import com.example.PhoneManagement.entity.Users;
import com.example.PhoneManagement.repository.RoleRepository;
import com.example.PhoneManagement.repository.AccountRepository;
import com.example.PhoneManagement.service.imp.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RoleServiceImp implements RoleService {
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private AccountRepository accountRepository;

    @Override
    public List<Roles> getAllRoles() {
        return roleRepository.findAll();
    }

    @Override
    public void changeUserRole(int userId, int newRoleId) {
        Optional<Users> userOptional = accountRepository.findById(userId);
        if (userOptional.isPresent()) {
            Users user = userOptional.get();
            Optional<Roles> roleOptional = Optional.ofNullable(roleRepository.findById(newRoleId));
            if (roleOptional.isPresent()) {
                Roles newRole = roleOptional.get();
                user.setRole(newRole);
                accountRepository.save(user);
            } else {
                throw new RuntimeException("Role not found with id: " + newRoleId);
            }
        } else {
            throw new RuntimeException("User not found with id: " + userId);
        }
    }
}
