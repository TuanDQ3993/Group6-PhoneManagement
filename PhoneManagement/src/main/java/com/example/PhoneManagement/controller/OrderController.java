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
import jakarta.validation.Valid;
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
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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

        Page<Object[]> ordersPage = orderService.getOrdersByUserIdWithFilters(userDTO.get(), status, searchQuery, page - 1, size);
        List<Object[]> list = ordersPage.getContent();

        Map<String, String> warrantyStatuses = new HashMap<>();
        Map<String, String> warrantyNote = new HashMap<>();
        Map<String, Date> warrantyDate = new HashMap<>();
        Map<String, Date> dateRepair = new HashMap<>();
        for (Object[] order : list) {
            Integer orderId = (Integer) order[0];
            Date orderDate = (Date) order[7];      // orderDate
            Integer warrantyPeriod = (Integer) order[10]; // warrantyPeriod
            String productName = (String) order[1];


            LocalDate createdAtDate = orderDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            LocalDate warrantyExpiryDate = createdAtDate.plusMonths(warrantyPeriod);
            boolean isWarrantyExpired = LocalDate.now().isAfter(warrantyExpiryDate);

            List<WarrantyRepair> warrantyRepairs = warrantyServiceImp.getByOrder(orderId);
            String warrantyStatus = null;
            String noteFromTechnical = null;
            Date dateWarranty = null;
            Date dateWarrantyRepair = null;

            for (WarrantyRepair repair : warrantyRepairs) {
                if (repair.getProductName().equals(productName)) {
                    warrantyStatus = repair.getStatus();
                    noteFromTechnical = repair.getNoteTechnical();
                    dateWarranty = repair.getDate_completed();
                    dateWarrantyRepair = repair.getRepairDate();
                }
            }

            warrantyStatuses.put(orderId + "_" + productName, warrantyStatus);
            warrantyNote.put(orderId + "_" + productName, noteFromTechnical);
            warrantyDate.put(orderId + "_" + productName, dateWarranty);
            model.addAttribute("orderId", orderId);
            model.addAttribute("productName", productName);
            model.addAttribute("warrantyExpiryDate", warrantyExpiryDate);
            model.addAttribute("isWarrantyExpired", isWarrantyExpired);

        }

        model.addAttribute("warrantyDTO", new WarrantyDTO());
        model.addAttribute("user", userDTO.get());
        model.addAttribute("page", page);
        model.addAttribute("size", size);
        model.addAttribute("searchQuery", searchQuery);
        model.addAttribute("status", status);
        model.addAttribute("groupedOrders", groupedOrders);
        model.addAttribute("orders", orders);
        model.addAttribute("warrantyStatuses", warrantyStatuses);
        model.addAttribute("noteFromTechnical", warrantyNote);
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
        return "redirect:/home/order";
    }

    // Phương thức tạo yêu cầu bảo hành mới
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
            model.addAttribute("error", "Product not found in order");
            return "orderlist";
        }


        // Reset trạng thái bảo hành nếu có yêu cầu bảo hành mới
        List<WarrantyRepair> existingRepairs = warrantyServiceImp.getByOrder(orderId);
        for (WarrantyRepair repair : existingRepairs) {
            if (repair.getStatus().equals("Warranty Completed")) {
                repair.setStatus("Warranty Pending");
                warrantyServiceImp.save(repair);
            }
        }

        int technicalId = warrantyServiceImp.getTechnicalMinOrder();
        Users technical = userService.getUserById(technicalId);
        warrantyServiceImp.createWarrantyRepair(warrantyDTO, order, users, technical, productName);

        return "redirect:/home/order";
    }



}
