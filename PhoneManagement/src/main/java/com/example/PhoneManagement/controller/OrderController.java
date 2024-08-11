package com.example.PhoneManagement.controller;

import com.example.PhoneManagement.dto.request.UserDTO;
import com.example.PhoneManagement.dto.request.WarrantyDTO;
import com.example.PhoneManagement.entity.OrderDetail;
import com.example.PhoneManagement.entity.Orders;
import com.example.PhoneManagement.entity.Users;
import com.example.PhoneManagement.entity.WarrantyRepair;
import com.example.PhoneManagement.service.OrderServiceImp;
import com.example.PhoneManagement.service.WarrantyServiceImp;
import com.example.PhoneManagement.service.imp.OrderService;
import com.example.PhoneManagement.service.imp.UserService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.time.ZoneId;
import java.util.*;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.time.LocalDate;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("/home")
public class OrderController {
    OrderService orderService;
    UserService userService;
    private final OrderServiceImp orderServiceImp;
    private final WarrantyServiceImp warrantyServiceImp;

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

        List<Object[]> orders = orderService.getOrdersByUserIdWithFilters(
                userDTO.get(), status, searchQuery);
        Map<Integer, List<Object[]>> groupedOrders = orders.stream()
                .collect(Collectors.groupingBy(order -> (Integer) order[0], LinkedHashMap::new, Collectors.toList()));

        List<Object[]> ordersPage = orderService.getOrdersByUserIdWithFilters(userDTO.get(), status, searchQuery);

        Map<String, String> warrantyStatuses = new HashMap<>();
        Map<String, String> warrantyNoteFromTechnical = new HashMap<>();
        Map<String, String> warrantyNoteFromCustomer = new HashMap<>();
        Map<String, Date> warrantyDate = new HashMap<>();
        Map<String, Date> dateRepair = new HashMap<>();
        Map<String, String> colorMap = new HashMap<>();
        for (Object[] order : ordersPage) {
            Integer orderId = (Integer) order[0];
            String productName = (String)  order[1];
            String productColor = (String) order[2];
            Date orderDate = null;
            Integer warrantyPeriod = null;
            if (order.length > 7) {
                orderDate = (Date) order[7]; // orderDate
            }
            if (order.length > 10) {
                warrantyPeriod = (Integer) order[10]; // warrantyPeriod
            }
            colorMap.put(orderId + "_" + productName, productColor);
            if (orderDate != null && warrantyPeriod != null) {
                LocalDate createdAtDate = orderDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                LocalDate warrantyExpiryDate = createdAtDate.plusMonths(warrantyPeriod);
                boolean isWarrantyExpired = LocalDate.now().isAfter(warrantyExpiryDate);
                List<WarrantyRepair> warrantyRepairs = warrantyServiceImp.getByOrder(orderId);
                String warrantyStatus = null;
                String noteFromTechnical = null;
                Date dateWarranty = null;
                Date dateWarrantyRepair = null;
                String noteFromCustomer = null;
                for (WarrantyRepair repair : warrantyRepairs) {
                    if (repair.getProductName().equals(productName)) {
                        warrantyStatus = repair.getStatus();
                        noteFromTechnical = repair.getNoteTechnical();
                        noteFromCustomer = repair.getIssueDescription();
                        dateWarranty = repair.getDate_completed();
                        dateWarrantyRepair = repair.getRepairDate();
                    }
                }

                warrantyStatuses.put(orderId + "_" + productName, warrantyStatus);
                warrantyNoteFromTechnical.put(orderId + "_" + productName, noteFromTechnical);
                warrantyNoteFromCustomer.put(orderId + "_" + productName, noteFromCustomer);
                warrantyDate.put(orderId + "_" + productName, dateWarranty);
                dateRepair.put(orderId + "_" + productName, dateWarrantyRepair);

                model.addAttribute("warrantyExpiryDate", warrantyExpiryDate);
                model.addAttribute("isWarrantyExpired", isWarrantyExpired);
            }

            model.addAttribute("orderId", orderId);
            model.addAttribute("productNameWithColor", productName);
        }
        model.addAttribute("colorMap", colorMap);
        model.addAttribute("warrantyDTO", new WarrantyDTO());
        model.addAttribute("user", userDTO.get());
        model.addAttribute("page", page);
        model.addAttribute("size", size);
        model.addAttribute("searchQuery", searchQuery);
        model.addAttribute("status", status);
        model.addAttribute("groupedOrders", groupedOrders);
        model.addAttribute("orders", orders);
        model.addAttribute("warrantyStatuses", warrantyStatuses);
        model.addAttribute("noteFromTechnical", warrantyNoteFromTechnical);
        model.addAttribute("noteFromCustomer", warrantyNoteFromCustomer);
        model.addAttribute("dateWarranty", warrantyDate);
        model.addAttribute("dateRepair", dateRepair);

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
        orderService.backProduct(oderId);
        return "redirect:/home/order";
    }

    @PostMapping("/createWarrantyRepair")
    public String createWarrantyRepair(@ModelAttribute @Valid WarrantyDTO warrantyDTO,
                                       @RequestParam("orderId") int orderId,
                                       @RequestParam("productName") String productName,
                                       BindingResult bindingResult,
                                       Authentication authentication,
                                       RedirectAttributes redirectAttributes,
                                       Model model) {
        if (bindingResult.hasErrors()) {
            return "orderlist";
        }

        Users users = (Users) authentication.getPrincipal();
        Optional<Orders> ordersOptional = Optional.ofNullable(orderServiceImp.getOrderInfo(orderId));
        if (ordersOptional.isEmpty()) {
            return "redirect:/home/homepage";
        }
        Orders order = ordersOptional.get();
        List<OrderDetail> orderDetails = order.getOrderDetails();

        // Tìm kiếm sản phẩm liên quan trong đơn hàng
        OrderDetail relevantOrderDetail = orderDetails.stream()
                .filter(od -> od.getProductInfo().getProducts().getProductName().equals(productName))
                .findFirst()
                .orElse(null);

        if (relevantOrderDetail == null) {
            model.addAttribute("error", "Product not found in order");
            return "orderlist";
        }


        int technicalId = warrantyServiceImp.getTechnicalMinOrder();
        Users technical = userService.getUserById(technicalId);
        warrantyServiceImp.createWarrantyRepair(warrantyDTO, order, users, technical, productName);


        redirectAttributes.addFlashAttribute("success", "Warranty repair created successfully.");
        return "redirect:/home/order";
    }


}
