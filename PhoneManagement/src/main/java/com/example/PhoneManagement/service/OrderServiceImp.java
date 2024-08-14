package com.example.PhoneManagement.service;

import com.example.PhoneManagement.dto.request.*;
import com.example.PhoneManagement.dto.response.ProductTopSeller;
import com.example.PhoneManagement.entity.*;
import com.example.PhoneManagement.repository.*;
import com.example.PhoneManagement.service.imp.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
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
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    ProductColorRepository productColorRepository;


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
    public Orders getOrderInfo(int orderId) {
        return orderRepository.findById(orderId).get();
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
            if (find != null && !find.trim().isEmpty()) {
                String searchTerm = find.trim().toLowerCase();
                filteredOrders = filteredOrders.stream()
                        .filter(order -> order.getUsername().toLowerCase().contains(searchTerm))
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
            if (find != null && !find.trim().isEmpty()) {
                String searchTerm = find.trim().toLowerCase();
                filteredOrders = filteredOrders.stream()
                        .filter(order -> order.getUsername().toLowerCase().contains(searchTerm))
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

    @Override
    public void changeSale(int orderId, int saleId) {
        Orders order = orderRepository.findById(orderId).orElseThrow(() -> new RuntimeException("Order Not Found"));
        order.setSalerId(saleId);
        orderRepository.save(order);
    }

    @Override
    public List<Object[]> getOrdersByUserIdWithFilters(UserDTO userDTO, String status , String search) {
        int userId = userRepository.findIdByUserName(userDTO.getUserName());
        return orderRepository.findOrdersWithFiltersCategory(userId, status,search);
    }


    @Override
    public int countTotalOrders(UserDTO user) {
        int userId = userRepository.findIdByUserName(user.getUserName());
        return orderRepository.countOrdersByUserId(userId);
    }

    @Override
    public List<Object[]> findOrderDetail(int orderId){
        return orderRepository.findOrderDetail(orderId);
    }

    @Override
    public void backProduct(int id) {
        Orders order= orderRepository.findById(id).get();
        for (OrderDetail orderDetail : order.getOrderDetails()) {
            ProductInfo productInfo = orderDetail.getProductInfo();
            productInfo.setQuantity(productInfo.getQuantity() + orderDetail.getQuantity());
            productColorRepository.save(productInfo);

            Products product=productInfo.getProducts();
            product.setQuantity(product.getQuantity() + orderDetail.getQuantity());
            productRepository.save(product);

        }
    }

    @Override
    public List<Users> getAllSale() {
        return userRepository.getAllSale();
    }



}