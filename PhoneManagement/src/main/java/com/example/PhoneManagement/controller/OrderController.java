package com.example.PhoneManagement.controller;

import com.example.PhoneManagement.dto.request.OrderInfoDTO;
import com.example.PhoneManagement.dto.request.PageableDTO;
import com.example.PhoneManagement.dto.request.UserDTO;
import com.example.PhoneManagement.dto.request.WarrantyDTO;
import com.example.PhoneManagement.entity.*;
import com.example.PhoneManagement.repository.ProductRepository;
import com.example.PhoneManagement.service.OrderServiceImp;
import com.example.PhoneManagement.service.WarrantyServiceImp;
import com.example.PhoneManagement.service.imp.OrderService;
import com.example.PhoneManagement.service.imp.UserService;
import jakarta.persistence.TypedQuery;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.catalina.User;
import org.springframework.cglib.core.Local;
import org.springframework.data.domain.Page;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.security.Principal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Controller
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("/home")
public class OrderController {
    OrderService orderService;
    UserService userService;
    ProductRepository productRepository;
    WarrantyServiceImp warrantyServiceImp;
    OrderServiceImp orderServiceImp;

    @GetMapping("/order")
    public String loadOrders(
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "size", defaultValue = "5") Integer size,
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "search", required = false) String searchQuery,
            Model model, Principal principal) {
        if (principal == null) {
            return "redirect:/auth/login";
        }

        String userName = principal.getName();
        Optional<UserDTO> userDTO = userService.getUserByUserName(userName);
        if (!userDTO.isPresent()) {
            return "redirect:/auth/login";
        }

        if (status == null || status.equals("all")) {
            status = "all";
        }

        Page<Object[]> orders = orderService.getOrdersByUserIdWithFilters(
                userDTO.get(), status, searchQuery, page - 1, size
        );
        Map<Integer, List<Object[]>> groupedOrders = orders.getContent().stream()
                .collect(Collectors.groupingBy(order -> (Integer) order[0]));

        Map<String, String> warrantyStatuses = new HashMap<>();
        Map<String, LocalDate> warrantyExpiryDates = new HashMap<>();
        Map<String, Boolean> isWarrantyExpiredMap = new HashMap<>();

        for (Map.Entry<Integer, List<Object[]>> entry : groupedOrders.entrySet()) {
            Integer orderId = entry.getKey();
            for (Object[] order : entry.getValue()) {
                Date orderDate = (Date) order[7];
                Integer warrantyPeriod = (Integer) order[10];
                String productName = (String) order[1];

                LocalDate createdAtDate = orderDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                LocalDate warrantyExpiryDate = createdAtDate.plusMonths(warrantyPeriod);
                boolean isWarrantyExpired = LocalDate.now().isAfter(warrantyExpiryDate);

                List<WarrantyRepair> warranties = warrantyServiceImp.getByOrder(orderId, productName);
                String statusOfOrder = warranties.isEmpty() ? "" : warranties.get(0).getStatus();

                String key = orderId + "_" + productName;
                warrantyStatuses.put(key, statusOfOrder);
                warrantyExpiryDates.put(key, warrantyExpiryDate);
                isWarrantyExpiredMap.put(key, isWarrantyExpired);
                model.addAttribute("productName", productName);
            }
            model.addAttribute("orderId", orderId);

        }

        model.addAttribute("warrantyStatuses", warrantyStatuses);
        model.addAttribute("warrantyExpiryDates", warrantyExpiryDates);
        model.addAttribute("isWarrantyExpiredMap", isWarrantyExpiredMap);
        model.addAttribute("warrantyDTO", new WarrantyDTO());
        model.addAttribute("user", userDTO.get());
        model.addAttribute("page", page);
        model.addAttribute("size", size);
        model.addAttribute("searchQuery", searchQuery);
        model.addAttribute("status", status);
        model.addAttribute("groupedOrders", groupedOrders);
        model.addAttribute("orders", orders);

        return "orderlist";
    }

    @GetMapping("/order/{orderId}")
    public String getOrderDetail(@PathVariable("orderId") int orderId, Model model) {
        List<Object[]> orders = orderService.findOrderDetail(orderId);
        Map<Integer, List<Object[]>> groupedOrders = orders.stream()
                .collect(Collectors.groupingBy(order -> (Integer) order[0]));
        model.addAttribute("orderdetail", groupedOrders);
        return "orderdetailuser";
    }

    @PostMapping("/order")
    public String updateStatusOrder(@RequestParam("orderId") int oderId) {
        orderService.changeStatusOrder(oderId, "Canceled");
        return "redirect:/home/order";
    }


    @PostMapping("/createWarrantyRepair")
    public String createWarrantyRepair(@ModelAttribute WarrantyDTO warrantyDTO,
                                       @RequestParam("orderId") int orderId,
                                       @RequestParam(value = "productName", required = false) String productName,
                                       Authentication authentication,
                                       Model model) {

        Users users = (Users) authentication.getPrincipal();
        Optional<Orders> ordersOptional = orderServiceImp.getOrderInfo(orderId);
        if (ordersOptional.isEmpty()) {
            return "redirect:/home/homepage";
        }
        Orders order = ordersOptional.get();
        List<OrderDetail> orderDetails = order.getOrderDetails();

        OrderDetail relevantOrderDetail = orderDetails.stream()
                .filter(od -> od.getProductInfo().getProducts().getProductName().equals(productName))
                .findFirst()
                .orElse(null);

        if (relevantOrderDetail == null) {
            return "redirect:/home/order";
        }



        int purchasedQuantity = relevantOrderDetail.getQuantity();
        int warrantyQuantity = warrantyDTO.getQuantity();
        if (warrantyQuantity > purchasedQuantity || warrantyQuantity == 0) {
            return "redirect:/home/order";
        }

        int technicalId = warrantyServiceImp.getTechnicalMinOrder();
        Users technical = userService.getUserById(technicalId);
        warrantyServiceImp.createWarrantyRepair(warrantyDTO, order, users, technical, productName);


        return "redirect:/home/order";
    }


}
