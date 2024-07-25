package com.example.PhoneManagement.service;

import com.example.PhoneManagement.dto.request.OrderDetailDTO;
import com.example.PhoneManagement.dto.request.OrderDetailInfoDTO;
import com.example.PhoneManagement.dto.request.OrderInfoDTO;
import com.example.PhoneManagement.dto.request.PageableDTO;
import com.example.PhoneManagement.repository.OrderDetailRepository;
import com.example.PhoneManagement.repository.OrderRepository;
import com.example.PhoneManagement.service.imp.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class OrderServiceImp implements OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderDetailRepository orderDetailRepository;


    @Override
    public List<OrderInfoDTO> getListOrder() {
        List<Object[]> results = orderRepository.findOrderedOrders();
        return results.stream().map(this::mapToOrderInfoDTO).collect(Collectors.toList());
    }

    @Override
    public OrderInfoDTO mapToOrderInfoDTO(Object[] result) {
        OrderInfoDTO dto = new OrderInfoDTO();
        dto.setOrderID(((Number) result[0]).intValue());
        dto.setProductName((String) result[1]);
        dto.setImage((String) result[2]);
        dto.setTotalAmount(((Number) result[3]).doubleValue());
        dto.setOrderDate(((Date) result[4]));
        dto.setUsername((String) result[5]);
        dto.setCountP(((Number) result[6]).intValue());
        return dto;
    }

    @Override
    public List<OrderDetailDTO> getOrderDetails(int orderId) {
        List<Object[]> results = orderDetailRepository.findAllOrderDetails(orderId);
        List<OrderDetailDTO> orderDetails = new ArrayList<>();
        for (Object[] result : results) {
            OrderDetailDTO dto = new OrderDetailDTO();
            dto.setOrderId(((Number) result[0]).intValue());
            dto.setProductId(((Number) result[1]).intValue());
            dto.setProductName((String) result[2]);
            dto.setImage((String) result[3]);
            dto.setQuantity(((Number) result[4]).intValue());
            dto.setPrice(((Number) result[5]).doubleValue());
            orderDetails.add(dto);
        }
        return orderDetails;
    }

    @Override
    public OrderDetailInfoDTO getOrderInfo(int orderId) {
        List<Object[]> results = orderRepository.findOrderInfo(orderId);
        Object[] result = results.get(0);
        OrderDetailInfoDTO dto = new OrderDetailInfoDTO();
        dto.setTotalAmount(((Number) result[0]).doubleValue());
        dto.setOrderDate((Date) result[1]);
        dto.setAddress((String) result[2]);
        dto.setCustomerName((String) result[3]);
        dto.setPhoneNumber((String) result[4]);
        return dto;
    }

    @Override
    public Page<OrderInfoDTO> findPaginated(PageableDTO pageable) {
        try {
            int pageSize = pageable.getPageSize();
            int currentPage = pageable.getPageNumber();
            int startItem = currentPage * pageSize;
            List<OrderInfoDTO> orders = getListOrder();
            List<OrderInfoDTO> list;

            if (orders.size() < startItem) {
                list = Collections.emptyList();
            } else {
                int toIndex = Math.min(startItem + pageSize, orders.size());
                list = orders.subList(startItem, toIndex);
            }

            return new PageImpl<OrderInfoDTO>(list, PageRequest.of(currentPage, pageSize), orders.size());
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }


}
