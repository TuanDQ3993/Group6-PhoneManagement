package com.example.PhoneManagement.controller;


import com.example.PhoneManagement.dto.request.PageDTO;
import com.example.PhoneManagement.entity.Users;
import com.example.PhoneManagement.entity.WarrantyRepair;
import com.example.PhoneManagement.service.WarrantyServiceImp;
import com.example.PhoneManagement.service.imp.WarrantyService;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Controller
@RequestMapping("/technical")
public class WarrantyController {

    @Autowired
    private WarrantyServiceImp warrantyRepairService;
    @GetMapping("/warranties")
    public String getAllWarranty(Model model,
                                 @RequestParam(defaultValue = "0") int page,
                                 @RequestParam(defaultValue = "5") int pageSize,
                                 Authentication authentication) {
        try {
            PageDTO pageDTO = new PageDTO();
            pageDTO.setPageNumber(page);
            pageDTO.setPageSize(pageSize);
            Users user = (Users) authentication.getPrincipal();
            String technicalName = null;
            Page<WarrantyRepair> warrantyPage = null;
            String role = user.getRole().getRoleName();

            if (user.getRole().getRoleName().equals("ADMIN")) {
                warrantyPage = warrantyRepairService.findAll(pageDTO);
            } else {
                warrantyPage = warrantyRepairService.findPaginated(pageDTO, user.getUserId());
                technicalName = user.getFullName();
            }
            System.out.println("id: "+user.getUserId());
            model.addAttribute("roleName",role);
            model.addAttribute("technicalName", technicalName);
            model.addAttribute("warrantyPage", warrantyPage);
            int totalPages = warrantyPage.getTotalPages();
            if (totalPages > 0) {
                List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPages).boxed().collect(Collectors.toList());
                model.addAttribute("pageNumbers", pageNumbers);
            }
            model.addAttribute("size", pageSize);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "listwarranty";
    }

    @GetMapping("/export/excel")
    public String exportToExcel(HttpServletResponse response, Authentication authentication,
                                @RequestParam("repairDate") String dateStr, RedirectAttributes redirectAttributes
                                ,Model model
                                ) throws IOException {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Date date;
        try {
            date = formatter.parse(dateStr);
        } catch (ParseException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi định dạng ngày");
            return "redirect:/technical/warranties";
        }

        Users users = (Users) authentication.getPrincipal();
        int technicalId = users.getUserId();


        if (!warrantyRepairService.isExistRepairDate(date)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Không có dữ liệu cho ngày được chọn.");
            return "redirect:/technical/warranties";
        }

        List<WarrantyRepair> warrantyRepairList = warrantyRepairService.getWarrantyByIdAndRepairDate(technicalId, new java.sql.Date(date.getTime()));

        response.setContentType("application/octet-stream");
        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
        String currentDateTime = dateFormatter.format(new java.util.Date());
        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=Danh_sach_bao_hanh_san_pham_" + currentDateTime + ".xlsx";
        response.setHeader(headerKey, headerValue);

        WarrantyExcel excelExporter = new WarrantyExcel(warrantyRepairList);
        excelExporter.export(response);

        return null;
    }

}
