package com.example.PhoneManagement.repository;

import com.example.PhoneManagement.entity.Roles;
import com.example.PhoneManagement.entity.Users;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.transaction.annotation.Transactional;

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
        userRepository.deleteAll();
        Users user = new Users();
        Roles role = roleRepository.findByRoleName("ADMIN");
        user.setUserName("haoquang11@gmail.com");
        user.setPassword("123");
        user.setFullName("haoquang11");
        user.setAddress("vinh");
        user.setPhoneNumber("0123456789");
        user.setRole(role);
        userRepository.save(user);
    }
    @Test
    void findByUserName() {
        //given


        //when
        Optional<Users> foundUser = userRepository.findByUserName("haoquang11@gmail.com");

        //then
        assertTrue(foundUser.isPresent());
        assertEquals(foundUser.get().getUserName(), "haoquang11@gmail.com");
        assertEquals(foundUser.get().getFullName(), "haoquang11");
    }

    @Test
    void existsByUserName() {
        //when
        Boolean exist = userRepository.existsByUserName("haoquang11@gmail.com");
        //then
        assertTrue(exist);
    }

    @Test
    public void testExistsByUserName_NotExisting() {
        // When
        Boolean exists = userRepository.existsByUserName("haoquang@gmail.com");

        // Then
        assertFalse(exists);
    }
}