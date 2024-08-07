package com.example.PhoneManagement.repository;

import com.example.PhoneManagement.entity.Roles;
import com.example.PhoneManagement.entity.Users;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
        roleRepository.deleteAll();
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
        accountRepository.flush();

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
        accountRepository.flush();
    }


    @Test
    public void testFindByRoleAndFullName() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Users> result = accountRepository.findByRoleAndFullName("Nguyen Hao Quang", testUser.getRole().getRoleId(), pageable);

        assertNotNull(result);
        assertEquals(result.getContent().get(0).getUserName(), testUser.getUserName());
    }

    @Test
    public void testFindByFullName() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Users> result = accountRepository.findByFullName("haoquang111@example.com", pageable);

        assertFalse(result.isEmpty(), "Result should not be empty");
        assertEquals(testUser2.getUserName(), result.getContent().get(0).getUserName());
    }

    @Test
    public void testFindByRole() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Users> result = accountRepository.findByRole(testUser.getRole().getRoleId(), pageable);

        assertFalse(result.isEmpty());
        assertEquals(2, result.getTotalElements());
        List<String> userNames = result.getContent().stream().map(Users::getUserName).toList();
        assertTrue(userNames.contains(testUser.getUserName()));
        assertTrue(userNames.contains(testUser2.getUserName()));
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
    public void testFindAllOrderByCreatedAt() {
        List<Users> result = accountRepository.findAllOrderByCreatedAt();

        assertFalse(result.isEmpty());
        assertEquals(testUser2.getUserName(), result.get(0).getUserName());
        assertEquals(testUser.getUserName(), result.get(1).getUserName());
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

    @Test
    public void testExistsByPhoneNumber() {
        boolean exists = accountRepository.existsByPhoneNumber(testUser.getPhoneNumber());
        assertTrue(exists);
    }

    @Test
    public void testExistsByUserName() {
        boolean exists = accountRepository.existsByUserName(testUser.getUserName());
        assertTrue(exists);
    }
}