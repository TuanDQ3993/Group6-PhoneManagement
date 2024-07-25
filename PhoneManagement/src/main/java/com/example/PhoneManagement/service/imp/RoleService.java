package com.example.PhoneManagement.service.imp;

import com.example.PhoneManagement.entity.Roles;

import java.util.List;

public interface RoleService {
    List<Roles> getAllRoles();
    void changeUserRole(int userId, int newRoleId);
}
