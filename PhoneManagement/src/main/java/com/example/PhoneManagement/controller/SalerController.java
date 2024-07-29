package com.example.PhoneManagement.controller;

import com.example.PhoneManagement.dto.request.OrderInfoDTO;
import com.example.PhoneManagement.dto.request.PageableDTO;
import com.example.PhoneManagement.dto.request.UserDTO;
import com.example.PhoneManagement.service.imp.OrderService;
import com.example.PhoneManagement.service.imp.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Controller
@RequestMapping("/saler")
public class SalerController {
    @Autowired
    private UserService userService;

    @Autowired
    private OrderService orderService;



    @GetMapping("/dashboard")
    public String loadDashboard(
            @RequestParam("page") Optional<Integer> page,
            @RequestParam("size") Optional<Integer> size,
            Model model, Principal principal) {
        String userName = principal.getName();
        Optional<UserDTO> userDTO = userService.getUserByUserName(userName);
        model.addAttribute("user", userDTO.get());

        int currentPage = page.orElse(1);
        int pageSize = size.orElse(5);

        model.addAttribute("size", pageSize);

        PageableDTO pageableDTO = new PageableDTO(currentPage-1, pageSize);
        Page<OrderInfoDTO> orderlist = orderService.findPaginated(pageableDTO);
        model.addAttribute("orderlist", orderlist);

        int totalPages = orderlist.getTotalPages();
        if (totalPages > 0) {
            List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPages)
                    .boxed()
                    .collect(Collectors.toList());
            model.addAttribute("pageNumbers", pageNumbers);
        }
        return "saler_dashboard";
    }

    @GetMapping("/orders/orderdetails/{id}")
    public String loadOrderDetails(Model model, Principal principal, @PathVariable int id) {
        String userName = principal.getName();
        Optional<UserDTO> userDTO = userService.getUserByUserName(userName);
        model.addAttribute("user", userDTO.get());
        model.addAttribute("orderdetail", orderService.getOrderDetails(id));
        model.addAttribute("orderinfo", orderService.getOrderInfo(id));
        return "orders_detail";
    }
}
