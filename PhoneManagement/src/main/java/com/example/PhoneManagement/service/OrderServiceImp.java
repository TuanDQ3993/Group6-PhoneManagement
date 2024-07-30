package com.example.PhoneManagement.service;

import com.example.PhoneManagement.dto.request.*;
import com.example.PhoneManagement.dto.response.ProductTopSeller;
import com.example.PhoneManagement.entity.Orders;
import com.example.PhoneManagement.repository.OrderDetailRepository;
import com.example.PhoneManagement.repository.OrderRepository;
import com.example.PhoneManagement.repository.ProductRepository;
import com.example.PhoneManagement.repository.UserRepository;
import com.example.PhoneManagement.service.imp.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class OrderServiceImp implements OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderDetailRepository orderDetailRepository;

    @Autowired
    private UserRepository userRepository;


    @Override
    public List<OrderInfoDTO> getListOrder() {
        List<Object[]> results = orderRepository.findOrderedOrders();
        return results.stream().map(this::mapToOrderInfoDTO).collect(Collectors.toList());
    }

    @Override
    public List<OrderInfoDTO> getListOrderBySaleId(int id) {
        List<OrderInfoDTO> orders = getListOrder();
        List<OrderInfoDTO> ordersBySale = orders.stream().filter(order -> id == order.getSalerId()).collect(Collectors.toList());
        return ordersBySale;
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
        dto.setStatus((String) result[7]);
        dto.setSalerId(((Number) result[8]).intValue());
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

    public LocalDate convertToLocalDate(Date date) {
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    @Override
    public Page<OrderInfoDTO> findPaginated(PageableDTO pageable, LocalDate startDate, LocalDate endDate, String status, String find, UserDTO user) {
        try {
            int pageSize = pageable.getPageSize();
            int currentPage = pageable.getPageNumber();
            int startItem = currentPage * pageSize;
            int userId = userRepository.findIdByUserName(user.getUserName());
            List<OrderInfoDTO> orders;
            if (user.getRoleName().equals("ADMIN")) {
                orders = getListOrder();
            } else {
                orders = getListOrderBySaleId(userId);
            }

            // Lọc theo ngày
            List<OrderInfoDTO> filteredOrders = orders.stream()
                    .filter(order -> {
                        LocalDate orderDate = convertToLocalDate(order.getOrderDate());
                        return !orderDate.isBefore(startDate) && !orderDate.isAfter(endDate);
                    })
                    .collect(Collectors.toList());

            // Lọc theo Status
            if (status != null && !status.isEmpty()) {
                filteredOrders = filteredOrders.stream().filter(order -> status.equals(order.getStatus()))
                        .collect(Collectors.toList());
            }

            //tìm theo tên
            if (find != null && !find.isEmpty()) {
                filteredOrders = filteredOrders.stream().filter(order -> find.trim().toLowerCase().equals(order.getUsername().toLowerCase()))
                        .collect(Collectors.toList());
            }

            List<OrderInfoDTO> list;

            if (orders.size() < startItem) {
                list = Collections.emptyList();
            } else {
                int toIndex = Math.min(startItem + pageSize, filteredOrders.size());
                list = filteredOrders.subList(startItem, toIndex);
            }

            return new PageImpl<OrderInfoDTO>(list, PageRequest.of(currentPage, pageSize), filteredOrders.size());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<OrderInfoDTO> getListOrderFilter(LocalDate startDate, LocalDate endDate, String status, String find, UserDTO user) {
        try {

            int userId = userRepository.findIdByUserName(user.getUserName());
            List<OrderInfoDTO> orders;
            if (user.getRoleName().equals("ADMIN")) {
                orders = getListOrder();
            } else {
                orders = getListOrderBySaleId(userId);
            }

            // Lọc theo ngày
            List<OrderInfoDTO> filteredOrders = orders.stream()
                    .filter(order -> {
                        LocalDate orderDate = convertToLocalDate(order.getOrderDate());
                        return !orderDate.isBefore(startDate) && !orderDate.isAfter(endDate);
                    })
                    .collect(Collectors.toList());

            // Lọc theo Status
            if (status != null && !status.isEmpty()) {
                filteredOrders = filteredOrders.stream().filter(order -> status.equals(order.getStatus()))
                        .collect(Collectors.toList());
            }

            //tìm theo tên
            if (find != null && !find.isEmpty()) {
                filteredOrders = filteredOrders.stream().filter(order -> find.equals(order.getUsername().toLowerCase()))
                        .collect(Collectors.toList());
            }

            return filteredOrders;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public long countOrdersInCurrentMonth() {
        LocalDate today = LocalDate.now();
        LocalDate firstDayOfMonth = today.withDayOfMonth(1);
        LocalDate lastDayOfMonth = today.withDayOfMonth(today.lengthOfMonth());

        // Chuyển đổi LocalDate sang Date
        Date startDate = Date.from(firstDayOfMonth.atStartOfDay(ZoneId.systemDefault()).toInstant());
        Date endDate = Date.from(lastDayOfMonth.atTime(23, 59, 59).atZone(ZoneId.systemDefault()).toInstant());

        return orderRepository.countByOrderDateBetween(startDate, endDate);
    }

    @Override
    public long countSalesInCurrentMonth() {
        LocalDate today = LocalDate.now();
        LocalDate firstDayOfMonth = today.withDayOfMonth(1);
        LocalDate lastDayOfMonth = today.withDayOfMonth(today.lengthOfMonth());

        // Chuyển đổi LocalDate sang Date
        Date startDate = Date.from(firstDayOfMonth.atStartOfDay(ZoneId.systemDefault()).toInstant());
        Date endDate = Date.from(lastDayOfMonth.atTime(23, 59, 59).atZone(ZoneId.systemDefault()).toInstant());

        return orderRepository.countByOrderDateBetweenAndStatus(startDate, endDate, "Completed");
    }

    @Override
    public Double getTotalAmountForCurrentMonth() {
        int currentMonth = LocalDate.now().getMonthValue();
        return orderRepository.sumTotalAmountByMonthAndStatus(currentMonth);
    }

    @Override
    public Double getTotalAmountForEachMonth(int month) {
        return orderRepository.sumTotalAmountByMonthAndStatus(month);
    }

    @Override
    public long countCustomerInCurrentMonth() {
        int currentMonth = LocalDate.now().getMonthValue();
        return orderRepository.countDistinctUserId(currentMonth);
    }

    @Override
    public List<ProductTopSeller> getProductTopSellers() {
        List<Object[]> results = orderDetailRepository.findTop5Sellers();
        List<ProductTopSeller> productTopSellers = new ArrayList<>();
        for (Object[] result : results) {
            ProductTopSeller productTopSeller = new ProductTopSeller();
            productTopSeller.setProductId(((Number) result[0]).intValue());
            productTopSeller.setProductName((String) result[1]);
            productTopSeller.setImage((String) result[2]);
            productTopSeller.setQuantitySold(((Number) result[3]).intValue());
            productTopSellers.add(productTopSeller);
        }
        return productTopSellers;
    }

    @Override
    public void changeStatusOrder(int orderId, String status) {
        Orders order = orderRepository.findById(orderId).orElseThrow(() -> new RuntimeException("Order Not Found"));
        order.setStatus(status);
        orderRepository.save(order);
    }


}
