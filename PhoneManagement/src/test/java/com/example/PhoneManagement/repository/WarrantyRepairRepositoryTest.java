package com.example.PhoneManagement.repository;

import com.example.PhoneManagement.entity.Orders;
import com.example.PhoneManagement.entity.Users;
import com.example.PhoneManagement.entity.WarrantyRepair;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class WarrantyRepairRepositoryTest {

    @Autowired
    private WarrantyRepairRepository warrantyRepairRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OrderRepository ordersRepository;

    private Users technical;
    private Users user;
    private Orders order;
    private WarrantyRepair warrantyRepair;

    @BeforeEach
    void setUp() {
        // Xóa dữ liệu trước khi thêm dữ liệu mới
        warrantyRepairRepository.deleteAll();
        ordersRepository.deleteAll();
        userRepository.deleteAll();

        // Tạo người dùng
        user = new Users();
        user.setUserName("user@example.com");
        user.setPassword("password");
        user.setFullName("User");
        user.setPhoneNumber("0123456789");
        user.setAddress("Vinh");
        user.setActive(true);
        userRepository.save(user);

        technical = new Users();
        technical.setUserName("tech@example.com");
        technical.setPassword("password");
        technical.setFullName("Tech");
        technical.setActive(true);
        technical.setPhoneNumber("0123456789");
        technical.setAddress("Ha Huy Tap");
        userRepository.save(technical);

        // Tạo đơn hàng
        order = new Orders();
        order.setOrderDate(new Date());
        order.setUser(user);
        ordersRepository.save(order);

        // Tạo WarrantyRepair
        warrantyRepair = new WarrantyRepair();
        warrantyRepair.setProductName("Product A");
        warrantyRepair.setImage("image_link");
        warrantyRepair.setStatus("Warranty Pending");
        warrantyRepair.setIssueDescription("Issue Description");
        warrantyRepair.setDeleted(false);
        warrantyRepair.setRepairDate(new Date());
        warrantyRepair.setUser(user);
        warrantyRepair.setTechnical(technical);
        warrantyRepair.setOrder(order);
        warrantyRepairRepository.save(warrantyRepair);
    }

    @Test
    void testFindAllBy() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<WarrantyRepair> page = warrantyRepairRepository.findAllBy(pageable);
        assertEquals(1, page.getTotalElements());
    }

    @Test
    void testFindPaginated() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<WarrantyRepair> page = warrantyRepairRepository.findPaginated(technical.getUserId(), pageable);
        assertEquals(1, page.getTotalElements());
    }

    @Test
    void testFindAllByTechnicalUserIdAndRepairDate() {
        Date repairDate = warrantyRepair.getRepairDate();
        List<WarrantyRepair> repairs = warrantyRepairRepository.findAllByTechnicalUserIdAndRepairDate(technical.getUserId(), repairDate);
        assertEquals(1, repairs.size());
    }

    @Test
    void testExistsWarrantyRepairsByRepairDate() {
        Date repairDate = warrantyRepair.getRepairDate();
        boolean exists = warrantyRepairRepository.existsWarrantyRepairsByRepairDate(repairDate);
        assertTrue(exists);
    }

    @Test
    void testSumWarrantyRepairs() {
        long count = warrantyRepairRepository.sumWarrantyRepairs(technical.getUserId());
        assertEquals(1, count);
    }

    @Test
    void testSumWarrantyRepairsByRepairDate() {
        Date repairDate = warrantyRepair.getRepairDate();
        long count = warrantyRepairRepository.sumWarrantyRepairsByRepairDate(technical.getUserId(), repairDate);
        assertEquals(1, count);
    }

    @Test
    void testCountWarrantyRepairsByRepairDateAndStatus() {
        Date repairDate = warrantyRepair.getRepairDate();
        long count = warrantyRepairRepository.countWarrantyRepairsByRepairDateAndStatus(technical.getUserId(), repairDate);
        assertEquals(0, count);
    }

    @Test
    void testCountAllWarrantyRepairsAndStatus() {
        long count = warrantyRepairRepository.countAllWarrantyRepairsAndStatus(technical.getUserId());
        assertEquals(0, count);
    }

    @Test
    void testFindCountByRepairDate() {
        List<Object[]> result = warrantyRepairRepository.findCountByRepairDate(technical.getUserId());
        assertEquals(1, result.size());
    }

    @Test
    void testFindWarrantyRepairByProductNameAndAndUser() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<WarrantyRepair> page = warrantyRepairRepository.findWarrantyRepairByProductNameAndAndUser("Product A", pageable, technical.getUserId());
        assertEquals(1, page.getTotalElements());
    }

    @Test
    void testFindAllByRepairDate() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<WarrantyRepair> page = warrantyRepairRepository.findAllByRepairDate(warrantyRepair.getRepairDate(), pageable, technical.getUserId());
        assertEquals(1, page.getTotalElements());
    }

    @Test
    void testFindAllByStatus() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<WarrantyRepair> page = warrantyRepairRepository.findAllByStatus("Warranty Pending", pageable, technical.getUserId());
        assertEquals(1, page.getTotalElements());
    }

    @Test
    void testFindAllByProductNameOrUserAndStatusAndRepairDateOrderByTechnical() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<WarrantyRepair> page = warrantyRepairRepository.findAllByProductNameOrUserAndStatusAndRepairDateOrderByTechnical("Product A", warrantyRepair.getRepairDate(), "Warranty Pending", pageable, technical.getUserId());
        assertEquals(1, page.getTotalElements());
    }

    @Test
    void testFindAllByProductNameOrUser() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<WarrantyRepair> page = warrantyRepairRepository.findAllByProductNameOrUser("Product A", pageable);
        assertEquals(1, page.getTotalElements());
    }

    @Test
    void testFindAllByRepairDateAdmin() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<WarrantyRepair> page = warrantyRepairRepository.findAllByRepairDate(warrantyRepair.getRepairDate(), pageable);
        assertEquals(1, page.getTotalElements());
    }

    @Test
    void testFindAllByStatusAdmin() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<WarrantyRepair> page = warrantyRepairRepository.findAllByStatus("Warranty Pending", pageable);
        assertEquals(1, page.getTotalElements());
    }

    @Test
    void testFindAllByProductNameOrUserAndStatusAndRepairDateAdmin() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<WarrantyRepair> page = warrantyRepairRepository.findAllByProductNameOrUserAndStatusAndRepairDate("Product A", warrantyRepair.getRepairDate(), "Warranty Pending", pageable);
        assertEquals(1, page.getTotalElements());
    }

    @Test
    void testFindAllByTechnicalUserIdAndRepairDateAdmin() {
        List<WarrantyRepair> repairs = warrantyRepairRepository.findAllByTechnicalUserIdAndRepairDate(warrantyRepair.getRepairDate());
        assertEquals(1, repairs.size());
    }
}