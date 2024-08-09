package com.example.PhoneManagement.controller;

import com.example.PhoneManagement.dto.request.OrderInfoDTO;
import com.example.PhoneManagement.dto.request.PageableDTO;
import com.example.PhoneManagement.dto.request.UserDTO;
import com.example.PhoneManagement.service.imp.OrderService;
import com.example.PhoneManagement.service.imp.UserService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import java.util.LinkedHashMap;
import org.springframework.data.domain.Page;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Controller
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("/home")
public class OrderController {
    OrderService orderService;
    UserService userService;

    @GetMapping("/order")
    public String loadOrders(
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "search", required = false) String searchQuery,
            Model model, Principal principal) {
        if (principal == null) {
            return "redirect:/auth/login";
        }
        String userName = principal.getName();
        Optional<UserDTO> userDTO = userService.getUserByUserName(userName);
        if (status == null || status.equals("all")) {
            status = "all";
        }
//        size=orderService.countTotalOrders(userDTO.get());
        List<Object[]> orders = orderService.getOrdersByUserIdWithFilters(
                userDTO.get(), status, searchQuery);


        Map<Integer, List<Object[]>> groupedOrders = orders.stream()
                .collect(Collectors.groupingBy(order -> (Integer) order[0], LinkedHashMap::new, Collectors.toList()));

        model.addAttribute("user", userDTO.get());
        model.addAttribute("searchQuery", searchQuery);
        model.addAttribute("status", status);
        model.addAttribute("groupedOrders", groupedOrders);
        model.addAttribute("orders",orders);

        return "orderlist";
    }

    @GetMapping("/order/{orderId}")
    public String getOrderDetail(@PathVariable("orderId") int orderId, Model model){
        List<Object[]> orders=orderService.findOrderDetail(orderId);
        Map<Integer, List<Object[]>> groupedOrders = orders.stream()
                .collect(Collectors.groupingBy(order -> (Integer) order[0]));
        model.addAttribute("orderdetail",groupedOrders);
        return "orderdetailuser";
    }

    @PostMapping("/order")
    public String updateStatusOrder(@RequestParam("orderId") int oderId){
        orderService.changeStatusOrder(oderId,"Canceled");
        orderService.backProduct(oderId);
        return "redirect:/home/order";
    }

}
