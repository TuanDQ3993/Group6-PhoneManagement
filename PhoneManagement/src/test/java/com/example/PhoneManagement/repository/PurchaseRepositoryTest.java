package com.example.PhoneManagement.repository;

import com.example.PhoneManagement.entity.Purchase;
import com.example.PhoneManagement.entity.Users;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
@DataJpaTest
class PurchaseRepositoryTest {
    @Autowired
    private PurchaseRepository purchaseRepository;

    @Autowired
    private UserRepository userRepository;

    private Users testUser;
    private Purchase testPurchase;

    @BeforeEach
    void setUp() {
        purchaseRepository.deleteAll();
        userRepository.deleteAll();

        testUser = new Users();
        testUser.setUserName("testuser@example.com");
        testUser.setPassword("password");
        testUser.setFullName("Test User");
        testUser.setAddress("123 Test St");
        testUser.setPhoneNumber("1234567890");
        userRepository.save(testUser);

        testPurchase = new Purchase();
        testPurchase.setUser(testUser);
        testPurchase.setTotalAmount(BigDecimal.valueOf(100.00));
        testPurchase.setPurchaseDate(new Date());
        purchaseRepository.save(testPurchase);
    }

    @Test
    void testFindByUserId() {
        Pageable pageable = PageRequest.of(0, 10);

        Page<Purchase> purchases = purchaseRepository.findByUserId(testUser.getUserId(), pageable);

        assertNotNull(purchases);
        assertTrue(purchases.hasContent());
        assertEquals(1, purchases.getTotalElements());
        assertEquals(testPurchase.getTotalAmount(), purchases.getContent().get(0).getTotalAmount());
        assertEquals(testPurchase.getUser().getUserId(), purchases.getContent().get(0).getUser().getUserId());
    }
//
//
//    @Test
//    void findDailyPurchasesForUser() {
//        List<Object[]> results = purchaseRepository.findDailyPurchasesForUser(1);
//        assertNotNull(results);
//        assertEquals(2, results.size());
//    }
//
//    @Test
//    void findFilteredDailyPurchasesForUser() {
//        List<Object[]> results = purchaseRepository.findFilteredDailyPurchasesForUser(
//                1, LocalDate.now().minusDays(1), LocalDate.now().plusDays(1), "Completed"
//        );
//        assertNotNull(results);
//        assertEquals(1, results.size());
//    }
}