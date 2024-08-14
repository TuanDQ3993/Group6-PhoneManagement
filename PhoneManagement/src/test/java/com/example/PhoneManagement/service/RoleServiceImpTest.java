package com.example.PhoneManagement.service;

import com.example.PhoneManagement.entity.Roles;
import com.example.PhoneManagement.entity.Users;
import com.example.PhoneManagement.repository.RoleRepository;
import com.example.PhoneManagement.repository.AccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RoleServiceImpTest {

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private RoleServiceImp roleService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllRoles() {

        Roles role1 = new Roles();
        Roles role2 = new Roles();
        when(roleRepository.findAll()).thenReturn(List.of(role1, role2));


        List<Roles> roles = roleService.getAllRoles();


        assertNotNull(roles);
        assertEquals(2, roles.size());
        verify(roleRepository, times(1)).findAll();
    }
    @Test
    void testChangeUserRole_UserAndRoleExist() {

        Users mockUser = new Users();
        Roles mockRole = new Roles();
        when(accountRepository.findById(1)).thenReturn(Optional.of(mockUser));
        when(roleRepository.findById(2)).thenReturn(mockRole);

        roleService.changeUserRole(1, 2);
        verify(accountRepository, times(1)).save(mockUser);
    }

    @Test
    void testChangeUserRole_UserNotFound() {

        when(accountRepository.findById(1)).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () -> roleService.changeUserRole(1, 2));
        assertEquals("User not found with id: 1", exception.getMessage());
        verify(accountRepository, never()).save(any(Users.class));
    }

    @Test
    void testChangeUserRole_RoleNotFound() {

        Users mockUser = new Users();
        when(accountRepository.findById(1)).thenReturn(Optional.of(mockUser));
        when(roleRepository.findById(2)).thenReturn(null);

        Exception exception = assertThrows(RuntimeException.class, () -> roleService.changeUserRole(1, 2));
        assertEquals("Role not found with id: 2", exception.getMessage());
        verify(accountRepository, never()).save(mockUser);
    }
    @Test
    void testGetRoleByName() {
        Roles mockRole = new Roles();
        when(roleRepository.findByRoleName("ADMIN")).thenReturn(mockRole);

        Roles role = roleService.getRoleByName("ADMIN");


        assertNotNull(role);
        verify(roleRepository, times(1)).findByRoleName("ADMIN");
    }
    @Test
    void testGetRoleById() {

        Roles mockRole = new Roles();
        when(roleRepository.findById(1)).thenReturn(mockRole);

        Roles role = roleService.getRoleById(1);
     assertNotNull(role);
        verify(roleRepository, times(1)).findById(1);
    }
    @Test
    void testGetDefaultRole() {

        Roles mockRole = new Roles();
        when(roleRepository.findById(4)).thenReturn(mockRole);

        Roles role = roleService.getDefaultRole();


        assertNotNull(role);
        verify(roleRepository, times(1)).findById(4);
    }

}
