package com.example.PhoneManagement.controller;

import com.example.PhoneManagement.service.DashboardService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class DashBoardController {
    @Autowired
    private DashboardService dashboardService;

//    @Autowired
//    private UserAccountRepository userAccountRepository;

    @GetMapping("/dashboard")
    public String getDashboard(@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                               @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
                               @RequestParam(required = false) String status,
                               Model model) {
//        String username = getUsername();
//        UserAccount user = userAccountRepository.findByUsername(username);

        List<Object[]> dailyPurchases = dashboardService.getFilteredDailyPurchasesForUser(3, startDate, endDate, status);
        List<Object[]> dailyPurchaseProduct = dashboardService.getDailyPurchaseProduct(3);

        List<String> productNames = dailyPurchaseProduct.stream()
                .map(dp -> (String) dp[0])
                .collect(Collectors.toList());

        List<Long> productCounts = dailyPurchaseProduct.stream()
                .map(dp -> (Long) dp[1])
                .collect(Collectors.toList());

        model.addAttribute("totalUsers", 2500);
        model.addAttribute("averageTime", 123.50);
        model.addAttribute("totalMales", 2500);
        model.addAttribute("totalFemales", 4567);
        model.addAttribute("totalCollections", 2315);
        model.addAttribute("totalConnections", 7325);

        model.addAttribute("labels", dailyPurchases.stream().map(dp -> dp[0].toString()).collect(Collectors.toList()));
        model.addAttribute("data", dailyPurchases.stream().map(dp -> dp[1]).collect(Collectors.toList()));

        model.addAttribute("campaignLabels", productNames);
        model.addAttribute("campaignData", productCounts);

        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);
        model.addAttribute("status", status);

        return "index";
    }

    @GetMapping("/export")
    public void exportDataToExcelTemplateFile(HttpServletResponse response) {
        try {
            response.setContentType("application/octet-stream");
            response.setHeader("Content-Disposition", "attachment; filename=exportPurchase.xlsx");
            dashboardService.exportDataToExcelTemplate(response.getOutputStream());
            response.flushBuffer();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

//    private String getUsername() {
//        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
//        if (principal instanceof UserDetails) {
//            return ((UserDetails) principal).getUsername();
//        } else {
//            return principal.toString();
//        }
//    }
}
