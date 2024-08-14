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
            String productColor = (String) order[2];  // Assuming color is stored as part of order details
            Date orderDate = null;
            Integer warrantyPeriod = null;

            if (order.length > 7) {
                orderDate = (Date) order[7];
            }
            if (order.length > 10) {
                warrantyPeriod = (Integer) order[10];
            }

            // Create a unique key including product name and color
            String productKey = orderId + "_" + productName + "_" + productColor;


            colorMap.put(productKey, productColor);
            String productNameColor = productName + " - " + productColor;

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
                    if (repair.getProductName().equals(productNameColor)) {  // Match by name
                        warrantyStatus = repair.getStatus().trim();
                        noteFromTechnical = repair.getNoteTechnical();
                        noteFromCustomer = repair.getIssueDescription();
                        dateWarranty = repair.getDate_completed();
                        dateWarrantyRepair = repair.getRepairDate();
                    }

                }

                warrantyStatuses.put(productNameColor, warrantyStatus);
                System.out.println("Warranty Statuses: " + warrantyStatuses);

                warrantyNoteFromTechnical.put(productNameColor, noteFromTechnical);
                warrantyNoteFromCustomer.put(productNameColor, noteFromCustomer);
                warrantyDate.put(productNameColor, dateWarranty);
                dateRepair.put(productNameColor, dateWarrantyRepair);
                model.addAttribute("warrantyExpiryDate", warrantyExpiryDate);
                model.addAttribute("isWarrantyExpired", isWarrantyExpired);
            }

            model.addAttribute("orderId", orderId);
            model.addAttribute("productNameWithColor", productName + " - " + productColor);
            model.addAttribute("productKey",productKey);
        }

        model.addAttribute("colorMap", colorMap);
        model.addAttribute("warrantyDTO", new WarrantyDTO());
        model.addAttribute("user", userDTO.get());
        model.addAttribute("page", page);
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
                                       @RequestParam("orderId") String orderIdStr,
                                       @RequestParam("productName") String productNameWithColor,
                                       BindingResult bindingResult,
                                       Authentication authentication,
                                       RedirectAttributes redirectAttributes,
                                       Model model) {
        // Validate the orderId input
        int orderId;
        try {
            orderId = Integer.parseInt(orderIdStr);
        } catch (NumberFormatException e) {
            model.addAttribute("error", "Invalid order ID. Please ensure you are submitting the form correctly.");
            return "orderlist";
        }

        // Handle validation errors in the DTO
        if (bindingResult.hasErrors()) {
            return "orderlist";
        }

        // Get the current authenticated user
        Users users = (Users) authentication.getPrincipal();

        // Fetch the order details by orderId
        Optional<Orders> ordersOptional = Optional.ofNullable(orderServiceImp.getOrderInfo(orderId));
        if (ordersOptional.isEmpty()) {
            return "redirect:/home/homepage"; // Redirect if the order is not found
        }

        Orders order = ordersOptional.get();
        List<OrderDetail> orderDetails = order.getOrderDetails();

        // Find the relevant product in the order
        OrderDetail relevantOrderDetail = orderDetails.stream()
                .filter(od -> (od.getProductInfo().getProducts().getProductName() + " - " + od.getProductInfo().getColors().getColorName())
                        .equals(productNameWithColor))
                .findFirst()
                .orElse(null);

        if (relevantOrderDetail == null) {
            model.addAttribute("error", "Product not found in order.");
            return "orderlist"; // Return to the order list if the product is not found
        }

        // Assign a technical person to handle the warranty repair
        int technicalId = warrantyServiceImp.getTechnicalMinOrder();
        Users technical = userService.getUserById(technicalId);

        // Create the warranty repair record
        warrantyServiceImp.createWarrantyRepair(warrantyDTO, order, users, technical, productNameWithColor);

        // Add a success message and redirect
        redirectAttributes.addFlashAttribute("success", "Warranty repair created successfully.");
        return "redirect:/home/order"; // Redirect to the order page
    }




}
