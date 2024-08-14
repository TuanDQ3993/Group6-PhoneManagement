package com.example.PhoneManagement.service;

import com.example.PhoneManagement.dto.request.PageDTO;
import com.example.PhoneManagement.dto.request.WarrantyDTO;
import com.example.PhoneManagement.entity.Orders;
import com.example.PhoneManagement.entity.Users;
import com.example.PhoneManagement.entity.WarrantyRepair;
import com.example.PhoneManagement.repository.WarrantyRepairRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WarrantyServiceImpTest {

    @Mock
    private WarrantyRepairRepository warrantyRepairRepository;

    @Mock
    private OrderServiceImp orderServiceImp;

    @InjectMocks
    private WarrantyServiceImp warrantyServiceImp;

    private WarrantyRepair warrantyRepair;
    private Users user;
    private Orders order;
    private PageDTO pageDTO;

    @BeforeEach
    void setUp() {
        user = new Users();
        order = new Orders();
        pageDTO = new PageDTO();
        pageDTO.setPageNumber(0);
        pageDTO.setPageSize(5);

        warrantyRepair = new WarrantyRepair();
        warrantyRepair.setWarrantyId(1);
        warrantyRepair.setProductName("Phone");
        warrantyRepair.setStatus("Warranty Pending");
        warrantyRepair.setUser(user);
        warrantyRepair.setOrder(order);
    }

    @Test
    void testFindAll() {
        Page<WarrantyRepair> page = new PageImpl<>(List.of(warrantyRepair));
        when(warrantyRepairRepository.findAllBy(any(PageRequest.class))).thenReturn(page);

        Page<WarrantyRepair> result = warrantyServiceImp.findAll(pageDTO);

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        verify(warrantyRepairRepository, times(1)).findAllBy(any(PageRequest.class));
    }

    @Test
    void testFindPaginated() {
        Page<WarrantyRepair> page = new PageImpl<>(List.of(warrantyRepair));
        when(warrantyRepairRepository.findPaginated(anyInt(), any(PageRequest.class))).thenReturn(page);

        Page<WarrantyRepair> result = warrantyServiceImp.findPaginated(pageDTO, 1);

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        verify(warrantyRepairRepository, times(1)).findPaginated(anyInt(), any(PageRequest.class));
    }

    @Test
    void testSave() {
        warrantyServiceImp.save(warrantyRepair);
        verify(warrantyRepairRepository, times(1)).save(warrantyRepair);
    }

    @Test
    void testDeleteWarrantyRepair() {
        when(warrantyRepairRepository.findById(anyInt())).thenReturn(Optional.of(warrantyRepair));
        warrantyServiceImp.deleteWarrantyRepair(1);

        assertTrue(warrantyRepair.isDeleted());
        verify(warrantyRepairRepository, times(1)).save(warrantyRepair);
    }

    @Test
    void testChangeStatus() {
        when(warrantyRepairRepository.findById(anyInt())).thenReturn(Optional.of(warrantyRepair));

        warrantyServiceImp.changeStatus(1, "In Process");

        assertEquals("In Process", warrantyRepair.getStatus());
        verify(warrantyRepairRepository, times(1)).save(warrantyRepair);
    }

    @Test
    void testAcceptWarranty() {
        when(warrantyRepairRepository.findById(anyInt())).thenReturn(Optional.of(warrantyRepair));

        warrantyServiceImp.acceptWarranty(1);

        assertEquals("In Process", warrantyRepair.getStatus());
        verify(warrantyRepairRepository, times(1)).save(warrantyRepair);
    }

    @Test
    void testCancelWarranty() {
        when(warrantyRepairRepository.findById(anyInt())).thenReturn(Optional.of(warrantyRepair));

        warrantyServiceImp.cancelWarranty(1);

        assertEquals("Warranty Cancel", warrantyRepair.getStatus());
        verify(warrantyRepairRepository, times(1)).save(warrantyRepair);
    }

    // Additional test cases can be written for other methods as needed.
}
