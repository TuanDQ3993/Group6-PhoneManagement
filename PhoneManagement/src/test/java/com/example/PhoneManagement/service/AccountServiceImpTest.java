package com.example.PhoneManagement.service;

import com.example.PhoneManagement.dto.request.PageDTO;
import com.example.PhoneManagement.entity.Users;
import com.example.PhoneManagement.repository.AccountRepository;
import com.example.PhoneManagement.repository.RoleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AccountServiceImpTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AccountServiceImp accountService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAll() {
        List<Users> usersList = List.of(new Users(), new Users());
        when(accountRepository.findAllOrderByCreatedAt()).thenReturn(usersList);

        List<Users> result = accountService.getAll();
        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    void testFindPaginated() {
        List<Users> usersList = List.of(new Users(), new Users(), new Users());
        PageDTO pageDTO = new PageDTO(0, 2);
        when(accountRepository.findAllOrderByCreatedAt()).thenReturn(usersList);

        Page<Users> result = accountService.findPaginated(pageDTO);
        assertNotNull(result);
        assertEquals(2, result.getContent().size());
        assertEquals(3, result.getTotalElements());
    }

    @Test
    void testBanUser() {
        doNothing().when(accountRepository).banUser(1);

        accountService.banUser(1);
        verify(accountRepository, times(1)).banUser(1);
    }

    @Test
    void testUnBanUser() {
        doNothing().when(accountRepository).unBanUser(1);

        accountService.unBanUser(1);
        verify(accountRepository, times(1)).unBanUser(1);
    }

    @Test
    void testGetUserById() {
        Users user = new Users();
        when(accountRepository.findById(1)).thenReturn(Optional.of(user));

        Users result = accountService.getUserById(1);
        assertNotNull(result);
    }

    @Test
    void testIsPhoneExist() {
        when(accountRepository.existsByPhoneNumber("0123456789")).thenReturn(true);

        boolean result = accountService.isPhoneExist("0123456789");
        assertTrue(result);
    }

    @Test
    void testIsEmailExist() {
        when(accountRepository.existsByUserName("test@example.com")).thenReturn(true);

        boolean result = accountService.isEmailExist("test@example.com");
        assertTrue(result);
    }

    @Test
    void testCreateUser() {
        Users user = new Users();
        when(accountRepository.save(user)).thenReturn(user);

        accountService.createUser(user);
        verify(accountRepository, times(1)).save(user);
    }

    @Test
    void testSearchAndFilterUsers() {
        Page<Users> usersPage = new PageImpl<>(Collections.singletonList(new Users()));
        PageDTO pageDTO = new PageDTO(0, 1);
        when(accountRepository.findByRoleAndFullName(anyString(), anyInt(), any(PageRequest.class))).thenReturn(usersPage);

        Page<Users> result = accountService.searchAndFilterUsers("John", pageDTO, 1);
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
    }

    @Test
    void testSearchUsers() {
        Page<Users> usersPage = new PageImpl<>(Collections.singletonList(new Users()));
        PageDTO pageDTO = new PageDTO(0, 1);
        when(accountRepository.findByFullName(anyString(), any(PageRequest.class))).thenReturn(usersPage);

        Page<Users> result = accountService.searchUsers("John", pageDTO);
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
    }

    @Test
    void testFilterRole() {
        Page<Users> usersPage = new PageImpl<>(Collections.singletonList(new Users()));
        PageDTO pageDTO = new PageDTO(0, 1);
        when(accountRepository.findByRole(anyInt(), any(PageRequest.class))).thenReturn(usersPage);

        Page<Users> result = accountService.filterRole(1, pageDTO);
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
    }

    @Test
    void testIsValidEmail() {
        assertTrue(accountService.isValidEmail("test@example.com"));
        assertFalse(accountService.isValidEmail("testexample.com"));
    }

    @Test
    void testIsValidPhoneNumber() {
        assertTrue(accountService.isValidPhoneNumber("0123456789"));
        assertFalse(accountService.isValidPhoneNumber("123456789"));
    }


    @Test
    void testCountAllUser() {
        when(accountRepository.countAllBy()).thenReturn(10L);

        long result = accountService.countAllUser();
        assertEquals(10, result);
    }
}
