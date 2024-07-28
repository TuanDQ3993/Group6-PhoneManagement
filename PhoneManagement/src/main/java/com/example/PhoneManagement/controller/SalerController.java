package com.example.PhoneManagement.controller;

import com.example.PhoneManagement.dto.request.OrderInfoDTO;
import com.example.PhoneManagement.dto.request.PageableDTO;
import com.example.PhoneManagement.dto.request.UserDTO;
import com.example.PhoneManagement.service.imp.OrderService;
import com.example.PhoneManagement.service.imp.UserService;
import com.example.PhoneManagement.util.OrderExcelExporter;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.security.Principal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
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
    public String dashboard(Model model,Principal principal) {
        String userName = principal.getName();
        Optional<UserDTO> userDTO = userService.getUserByUserName(userName);
        model.addAttribute("user", userDTO.get());
        model.addAttribute("order",orderService.countOrdersInCurrentMonth());
        model.addAttribute("sale",orderService.countSalesInCurrentMonth());
        model.addAttribute("totalamount",orderService.getTotalAmountForCurrentMonth());
        model.addAttribute("customer",orderService.countCustomerInCurrentMonth());
        model.addAttribute("topseller",orderService.getProductTopSellers());

        List<Double> totalAmounts = new ArrayList<>();
        for (int month = 1; month <= 12; month++) {
            Double totalAmount = orderService.getTotalAmountForEachMonth(month);
            totalAmounts.add(totalAmount);
        }
        model.addAttribute("totalAmounts", totalAmounts);
        return "saler_dashboard";
    }

    @GetMapping("/orders")
    public String loadOrders(
            @RequestParam("page") Optional<Integer> page,
            @RequestParam("size") Optional<Integer> size,
            @RequestParam("startDate") Optional<String> startDate,
            @RequestParam("endDate") Optional<String> endDate,
            @RequestParam("status") Optional<String> status,
            @RequestParam("searchQuery") Optional<String> searchQuery,
            Model model, Principal principal
            ) {
        String userName = principal.getName();
        Optional<UserDTO> userDTO = userService.getUserByUserName(userName);
        model.addAttribute("user", userDTO.get());

        int currentPage = page.orElse(1);
        int pageSize = size.orElse(5);
        String statusO=status.orElse(null);
        String query = searchQuery.orElse("");



        LocalDate start = startDate.map(LocalDate::parse).orElse(LocalDate.now().minusMonths(1));
        LocalDate end = endDate.map(LocalDate::parse).orElse(LocalDate.now());

        PageableDTO pageableDTO = new PageableDTO(currentPage-1, pageSize);
        Page<OrderInfoDTO> orderlist = orderService.findPaginated(pageableDTO, start, end,statusO,query,userDTO.get());


        model.addAttribute("size", pageSize);
        model.addAttribute("searchQuery", query);
        model.addAttribute("status", status.orElse(null));
        model.addAttribute("orderlist", orderlist);
        model.addAttribute("startDate", start);
        model.addAttribute("endDate", end);
        int totalPages = orderlist.getTotalPages();
        if (totalPages > 0) {
            List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPages)
                    .boxed()
                    .collect(Collectors.toList());
            model.addAttribute("pageNumbers", pageNumbers);
        }
        return "saler_orderlist";
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

    @GetMapping("/changestatus")
    public String changeStatus(Model model, Principal principal,
                               @RequestParam("oid") int oid,
                               @RequestParam("status") String status,
                               @RequestParam("value") int value
                               ){
        if(value==2){
            orderService.changeStatusOrder(oid,"Confirm and Shipping");
        }
        else if(value==1){
            orderService.changeStatusOrder(oid,"Completed");
        }
        else{
            orderService.changeStatusOrder(oid,"Cancelled");
        }


        return "redirect:/saler/orders";
    }

    @GetMapping("/export")
    public void exportToExcel(HttpServletResponse response,
                                @RequestParam("startDate") Optional<String> startDate,
                                @RequestParam("endDate") Optional<String> endDate,
                                @RequestParam("status") String status,
                                @RequestParam("searchQuery") String searchQuery,
                                Principal principal) throws IOException {
        response.setContentType("application/octet-stream");
        String headerKey="Content-Disposition";

        DateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd");
        String currentDateTime=dateFormat.format(new Date());
        String fileName="orders_"+currentDateTime+".xlsx";
        String headerValue="attachment; filename= "+fileName;
        response.setHeader(headerKey, headerValue);

        String userName = principal.getName();
        Optional<UserDTO> userDTO = userService.getUserByUserName(userName);
        LocalDate start = startDate.map(LocalDate::parse).orElse(LocalDate.now().minusMonths(1));
        LocalDate end = endDate.map(LocalDate::parse).orElse(LocalDate.now());
        List<OrderInfoDTO> listOrders=orderService.getListOrderFilter(start,end,status,searchQuery,userDTO.get());


        OrderExcelExporter excelExporter=new OrderExcelExporter(listOrders);
        excelExporter.export(response);
    }
}
