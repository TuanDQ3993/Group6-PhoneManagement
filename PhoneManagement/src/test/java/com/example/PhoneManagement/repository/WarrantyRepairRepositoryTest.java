package com.example.PhoneManagement.repository;

import com.example.PhoneManagement.entity.Users;
import com.example.PhoneManagement.entity.WarrantyRepair;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class WarrantyRepairRepositoryTest {


    @Autowired
    private WarrantyRepairRepository warrantyRepairRepository;

    @Autowired
    private UserRepository userRepository;

    private Users user;
    private WarrantyRepair repair;
    private WarrantyRepair repair2;


    @BeforeEach
    void setUp() {
        warrantyRepairRepository.deleteAll();
        userRepository.deleteAll();

        user = new Users();
        user.setUserName("haoquang@example.com");
        user.setPassword("123");
        user.setFullName("Quang Test");
        user.setAddress("Vinh Test");
        user.setPhoneNumber("1234567890");
        userRepository.save(user);

        repair = new WarrantyRepair();
        repair.setUser(user);
        repair.setProductName("Product Test");
        repair.setImage("test.png");
        repair.setStatus("test_status");
        repair.setTechnicalId(1);
        repair.setRepairDate(new Date());
        repair.setIssueDescription("This is a test");
        warrantyRepairRepository.save(repair);

        repair2 = new WarrantyRepair();
        repair2.setUser(user);
        repair2.setProductName("Product Test");
        repair2.setImage("test.png");
        repair2.setStatus("test_status");
        repair2.setTechnicalId(1);
        repair2.setRepairDate(new Date());
        repair2.setIssueDescription("This is a test2");
        warrantyRepairRepository.save(repair2);

    }

    @Test
    void findAllById() {
        List<WarrantyRepair> repairs = warrantyRepairRepository.findAll();
        assertNotNull(repairs);
        assertEquals(2, repairs.size());
        assertEquals(1, repairs.get(0).getTechnicalId());

    }

    @Test
    void searchWarrantyRepairByName() {
        List<WarrantyRepair> repairs = warrantyRepairRepository.searchWarrantyRepairByName("Quang Test"); // Tham số chính xác
        assertNotNull(repairs);
        assertFalse(repairs.isEmpty());
        assertTrue(repairs.stream().allMatch(repair -> repair.getUser().getFullName().equalsIgnoreCase("Quang Test")));
    }
}