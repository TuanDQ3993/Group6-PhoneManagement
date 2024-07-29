package com.example.PhoneManagement.repository;

import com.example.PhoneManagement.entity.Roles;
import com.example.PhoneManagement.entity.Users;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@DataJpaTest
class AccountRepositoryTest {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private RoleRepository roleRepository;

    private Users testUser;
    private Users testUser2;
    @Autowired
    private TestEntityManager testEntityManager;

    @BeforeEach
    void setUp() {
        accountRepository.deleteAll();
        Roles role = new Roles();
        role.setRoleName("USER");
        role = roleRepository.save(role);

        //Person1
        testUser = new Users();
        testUser.setUserName("haoquang@example.com");
        testUser.setPassword("123456");
        testUser.setFullName("Nguyen Hao Quang");
        testUser.setPhoneNumber("1234567890");
        testUser.setAddress("Ha Huy Tap");
        testUser.setRole(role);
        testUser.setActive(true);
        testUser.setCreatedAt(Date.from(LocalDateTime.now().minusDays(1).atZone(ZoneId.systemDefault()).toInstant()));
        testUser = accountRepository.save(testUser);

        //Person2
        testUser2 = new Users();
        testUser2.setUserName("haoquang111@example.com");
        testUser2.setPassword("123456");
        testUser2.setFullName("Nguyen Hao Con");
        testUser2.setPhoneNumber("1234567890");
        testUser2.setAddress("Vinh");
        testUser2.setRole(role);
        testUser2.setActive(true);
        testUser2.setCreatedAt(Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()));
        testUser2 = accountRepository.save(testUser2);
    }


    @Test
    void unBanUser() {
        accountRepository.unBanUser(testUser.getUserId());
        Users userUpdated = accountRepository.findById(testUser.getUserId()).orElseThrow();
        assertTrue(userUpdated.isActive());
    }

    @Test
    void banUser() {
        accountRepository.banUser(testUser.getUserId());
        testEntityManager.flush();
        testEntityManager.clear();
        Users userUpdated = accountRepository.findById(testUser.getUserId()).orElseThrow();
        assertFalse(userUpdated.isActive());
    }

    @Test
    void findAllOrderByCreatedAt() {

        List<Users> users = accountRepository.findAllOrderByCreatedAt();
        assertFalse(users.isEmpty());
        assertEquals(2, users.size());
        assertEquals(testUser.getFullName(), users.get(0).getFullName());
        assertEquals(testUser2.getFullName(), users.get(1).getFullName());

    }

    @Test
    void changeRole() {
        Roles newRole = new Roles();
        newRole.setRoleName("ADMIN");
        newRole = roleRepository.save(newRole);
        accountRepository.changeRole(testUser.getUserId(), newRole.getRoleId());
        testEntityManager.flush();
        testEntityManager.clear();
        Optional<Users> user = accountRepository.findById(testUser.getUserId());
        assertTrue(user.isPresent());
        assertEquals(newRole.getRoleId(), user.get().getRole().getRoleId());
    }
}