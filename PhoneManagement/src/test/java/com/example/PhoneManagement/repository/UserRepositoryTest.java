package com.example.PhoneManagement.repository;

import com.example.PhoneManagement.entity.Roles;
import com.example.PhoneManagement.entity.Users;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
@DataJpaTest
@Transactional
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;

    @BeforeEach
    void setUp() {
        // Xóa dữ liệu trước khi thêm dữ liệu mới
        userRepository.deleteAll();
        roleRepository.deleteAll();

        // Tạo role
        Roles role1 = new Roles();
        role1.setRoleId(1);
        role1.setRoleName("Admin");
        roleRepository.save(role1);

        Roles role2 = new Roles();
        role2.setRoleId(2);
        role2.setRoleName("Sales");
        roleRepository.save(role2);

        // Tạo người dùng
        Users user1 = new Users();
        user1.setUserName("sales1@example.com");
        user1.setPassword("password");
        user1.setFullName("Sales User 1");
        user1.setActive(true);
        user1.setRole(role2); // Sales role
        user1.setAddress("Vinh");
        user1.setPhoneNumber("0123456789");
        userRepository.save(user1);

        Users user2 = new Users();
        user2.setUserName("admin@example.com");
        user2.setPassword("password");
        user2.setFullName("Admin User");
        user2.setActive(true);
        user2.setPhoneNumber("0123456789");
        user2.setAddress("Ha Huy Tap");
        user2.setRole(role1); // Admin role
        userRepository.save(user2);
    }

    @Test
    void testFindByUserName() {
        Optional<Users> user = userRepository.findByUserName("sales1@example.com");
        assertTrue(user.isPresent());
        assertEquals("Sales User 1", user.get().getFullName());
    }

    @Test
    void testExistsByUserName() {
        assertTrue(userRepository.existsByUserName("sales1@example.com"));
        assertFalse(userRepository.existsByUserName("nonexistent@example.com"));
    }

    @Test
    void testFindIdByUserName() {
        int userId = userRepository.findIdByUserName("sales1@example.com");
        assertTrue(userId > 0);
    }

    @Test
    void testGetAllSale() {
        List<Users> salesUsers = userRepository.getAllSale();
        assertNotNull(salesUsers);
        assertFalse(salesUsers.isEmpty());
        assertEquals(1, salesUsers.size()); // Chỉ có một người dùng thuộc vai trò Sales
    }
}