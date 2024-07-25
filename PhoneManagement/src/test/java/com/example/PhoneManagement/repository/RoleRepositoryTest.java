package com.example.PhoneManagement.repository;

import com.example.PhoneManagement.entity.Roles;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
@Transactional
@DataJpaTest
class RoleRepositoryTest {

    @Autowired
    private RoleRepository roleRepository;

    @BeforeEach
    void setUp() {
        roleRepository.deleteAll();
        Roles adminRole = new Roles();
        adminRole.setRoleId(1);
        adminRole.setRoleName("ADMIN");
        roleRepository.save(adminRole);

        Roles userRole = new Roles();
        userRole.setRoleId(2);
        userRole.setRoleName("USER");
        roleRepository.save(userRole);
    }

    @Test
    void findById() {
        //given
        int roleId = 1;
        //when
        Roles foundRole = roleRepository.findById(roleId);
        //then
        assertEquals(roleId, foundRole.getRoleId());
        assertEquals("ADMIN", foundRole.getRoleName());
    }

    @Test
    void findByRoleName() {
        //given
        String roleName = "USER";
        //when
        Roles foundRole = roleRepository.findByRoleName(roleName);
        //then
        assertNotNull(foundRole);
        assertEquals(roleName, foundRole.getRoleName());
    }
}