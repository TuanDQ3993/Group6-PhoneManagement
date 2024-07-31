package com.example.PhoneManagement.controller;

import com.example.PhoneManagement.dto.request.OrderInfoDTO;
import com.example.PhoneManagement.dto.request.PageableDTO;
import com.example.PhoneManagement.dto.request.UserDTO;
import com.example.PhoneManagement.service.imp.OrderService;
import com.example.PhoneManagement.service.imp.UserService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.cglib.core.Local;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Controller
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
@RequestMapping("/home")
public class OrderController {
    OrderService orderService;
    UserService userService;

    @GetMapping("/order")
    public String loadOrders(
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "size", defaultValue = "5") Integer size,
            @RequestParam(value = "startDate" ,required = false) LocalDate startDate,
            @RequestParam(value = "endDate", required = false) LocalDate endDate,
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "search", required = false) String searchQuery,
            Model model, Principal principal) {
        String userName = principal.getName();
        Optional<UserDTO> userDTO = userService.getUserByUserName(userName);

            Integer userId = userDTO.get().getUserId();
            Page<Object[]> orders = orderService.getOrdersByUserIdWithFilters(
                    1, status, startDate, endDate, page - 1, size
            );

            // Group orders by orderId
            Map<Integer, List<Object[]>> groupedOrders = orders.getContent().stream()
                    .collect(Collectors.groupingBy(order -> (Integer) order[0])); // Assuming order[0] is orderId

            model.addAttribute("user", userDTO.get());
            model.addAttribute("page", page);
            model.addAttribute("size", size);
            model.addAttribute("searchQuery", searchQuery);
            model.addAttribute("status", status);
            model.addAttribute("groupedOrders", groupedOrders); // Pass grouped orders to the view
            model.addAttribute("startDate", startDate);
            model.addAttribute("endDate", endDate);

            return "orderlist";
    }
}
