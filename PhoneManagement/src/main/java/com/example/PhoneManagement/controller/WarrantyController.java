package com.example.PhoneManagement.controller;


import com.example.PhoneManagement.dto.request.PageDTO;
import com.example.PhoneManagement.dto.request.UserDTO;
import com.example.PhoneManagement.entity.Users;
import com.example.PhoneManagement.entity.WarrantyRepair;
import com.example.PhoneManagement.service.WarrantyServiceImp;
import com.example.PhoneManagement.service.imp.UserService;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Controller
@RequestMapping("/technical")
public class WarrantyController {

    @Autowired
    private WarrantyServiceImp warrantyRepairService;
    @Autowired
    private UserService userService;

    @GetMapping("/dashboard_technical")
    public String dashBoard(Model model, Authentication authentication) {
        Users user = (Users) authentication.getPrincipal();
        int technicalId = user.getUserId();
        Map<String, Long> warrantyCountByDate = warrantyRepairService.getWarrantyCountByDate(technicalId);
        LocalDate localDate = LocalDate.now();

        Date date = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
        model.addAttribute("warrantyCountByDate", warrantyCountByDate);
        model.addAttribute("sumWarrantyRepairsByDate", warrantyRepairService.sumWarrantyRepairsByRepairDate(technicalId, date));
        model.addAttribute("sumWarrantyRepairsAll", warrantyRepairService.sumWarrantyRepairs(technicalId));
        model.addAttribute("countWarrantyRepairsByRepairDateAndStatus", warrantyRepairService.countWarrantyRepairsByRepairDateAndStatus(technicalId, date));
        model.addAttribute("countAllWarrantyRepairsAndStatus", warrantyRepairService.countAllWarrantyRepairsAndStatus(technicalId));
        return "dashboard_technical";
    }

    @GetMapping("/warranties")
    public String getAllWarranty(Model model,
                                 @RequestParam(defaultValue = "0") int page,
                                 @RequestParam(defaultValue = "5") int pageSize,
                                 Authentication authentication,
                                 @RequestParam(value = "repairDate", required = false) String dateStr,
                                 @RequestParam(value = "query", required = false) String query,
                                 @RequestParam(value = "status", required = false) String status,
                                 RedirectAttributes redirectAttributes) {
        try {
            Date date = null;
            String repairDateStr = "";
            if (dateStr != null && !dateStr.isEmpty()) {
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                try {
                    date = formatter.parse(dateStr);
                    repairDateStr = dateStr; // Keep the original date string to display in the view
                } catch (ParseException e) {
                    redirectAttributes.addFlashAttribute("errorMessage", "Invalid date format");
                    return "redirect:/technical/warranties";
                }
            }

            PageDTO pageDTO = new PageDTO();
            pageDTO.setPageNumber(page);
            pageDTO.setPageSize(pageSize);
            Users user = (Users) authentication.getPrincipal();
            String technicalName = null;
            Page<WarrantyRepair> warrantyPage = null;
            String role = user.getRole().getRoleName();

            if (role.equals("ADMIN")) {
                if (date != null && query != null && !query.isEmpty() && status != null && !status.isEmpty()) {
                    warrantyPage = warrantyRepairService.findAllByProductNameAndStatusAndDateByAdmin(pageDTO, status, date, query);
                } else if (date != null && query != null && !query.isEmpty()) {
                    warrantyPage = warrantyRepairService.findAllByProductNameAndRepairDateByAdmin( query, date,pageDTO);
                } else if (date != null && status != null && !status.isEmpty()) {
                    warrantyPage = warrantyRepairService.findAllByStatusAndRepairDateByAdmin(status, date,pageDTO);
                } else if (query != null && !query.isEmpty() && status != null && !status.isEmpty()) {
                    warrantyPage = warrantyRepairService.findAllByProductNameAndStatusByAdmin( query, status,pageDTO);
                } else if (date != null) {
                    warrantyPage = warrantyRepairService.findByRepairDateByAdmin(pageDTO, date);
                } else if (query != null && !query.isEmpty()) {
                    warrantyPage = warrantyRepairService.findAllByProductNameByAdmin(pageDTO, query);
                } else if (status != null && !status.isEmpty()) {
                    warrantyPage = warrantyRepairService.findByStatusByAdmin(pageDTO, status);
                } else {
                    warrantyPage = warrantyRepairService.findAll(pageDTO);
                }
            } else {
                technicalName = user.getFullName();
                if (date != null && query != null && !query.isEmpty() && status != null && !status.isEmpty()) {
                    warrantyPage = warrantyRepairService.findAllByProductNameAndStatusAndDateByTechnical(query, status,  date,user.getUserId(), pageDTO);
                } else if (date != null && query != null && !query.isEmpty()) {
                    warrantyPage = warrantyRepairService.findAllByProductNameAndRepairDateByTechnical(query, date, user.getUserId(), pageDTO);
                } else if (date != null && status != null && !status.isEmpty()) {
                    warrantyPage = warrantyRepairService.findAllByStatusAndRepairDateByTechnical(status, date, user.getUserId(), pageDTO);
                } else if (query != null && !query.isEmpty() && status != null && !status.isEmpty()) {
                    warrantyPage = warrantyRepairService.findAllByProductNameAndStatusByTechnical(query, status, user.getUserId(),pageDTO);
                } else if (date != null) {
                    warrantyPage = warrantyRepairService.findByRepairDate(pageDTO, date, user.getUserId());
                } else if (query != null && !query.isEmpty()) {
                    warrantyPage = warrantyRepairService.findByProductNameAndUserName(pageDTO, query, user.getUserId());
                } else if (status != null && !status.isEmpty()) {
                    warrantyPage = warrantyRepairService.findByStatus(pageDTO, status, user.getUserId());
                } else {
                    warrantyPage = warrantyRepairService.findPaginated(pageDTO, user.getUserId());
                }
            }

            // Add attributes to model
            model.addAttribute("status", status);
            model.addAttribute("roleName", role);
            model.addAttribute("technicalName", technicalName);
            model.addAttribute("warrantyPage", warrantyPage);
            model.addAttribute("repairDate", repairDateStr);
            model.addAttribute("query", query);

            int totalPages = warrantyPage.getTotalPages();
            if (totalPages > 0) {
                List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPages).boxed().collect(Collectors.toList());
                model.addAttribute("pageNumbers", pageNumbers);
            }
            model.addAttribute("size", pageSize);
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("errorMessage", "A system error has occurred");
            return "redirect:/technical/warranties";
        }
        return "listwarranty";
    }



    @GetMapping("/export/excel")
    public String exportToExcel(HttpServletResponse response, Authentication authentication,
                                @RequestParam("repairDate") String dateStr, RedirectAttributes redirectAttributes
            , Model model
    ) throws IOException {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Date date;
        try {
            date = formatter.parse(dateStr);
        } catch (ParseException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Date invalid");
            return "redirect:/technical/warranties";
        }

        Users users = (Users) authentication.getPrincipal();
        int technicalId = users.getUserId();


        if (!warrantyRepairService.isExistRepairDate(date)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Data not exist.");
            return "redirect:/technical/warranties";
        }
        List<WarrantyRepair> warrantyRepairList = null;
        if (users.getRole().getRoleName().equals("ADMIN")) {
            warrantyRepairList = warrantyRepairService.getWarrantyByIdAndRepairDateByAdmin(date);
        } else {
            warrantyRepairList = warrantyRepairService.getWarrantyByIdAndRepairDate(technicalId, date);
        }


        response.setContentType("application/octet-stream");
        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
        String currentDateTime = dateFormatter.format(new Date());
        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=Danh_sach_bao_hanh_san_pham_" + currentDateTime + ".xlsx";
        response.setHeader(headerKey, headerValue);

        WarrantyExportExcel excelExporter = new WarrantyExportExcel(warrantyRepairList);
        excelExporter.export(response);

        return null;
    }


    @PostMapping("/changeStatus/{id}")
    public String changeStatusAndSaveNote(
            @PathVariable int id,
            @RequestParam(value = "status") String status,
            @RequestParam(value = "noteTechnical") String note) {
        warrantyRepairService.changeStatusAndSaveNote(id, status, note);
        return "redirect:/technical/warranties";
    }


    @PostMapping("/acceptWarranty/{id}")
    public String acceptWarranty(
            @PathVariable int id) {
        warrantyRepairService.acceptWarranty(id);
        warrantyRepairService.changeStatus(id, "Warranty In Process");
        return "redirect:/technical/warranties";
    }

    @PostMapping("/cancelWarranty/{id}")
    public String rejectWarranty(
            @PathVariable int id) {
        warrantyRepairService.cancelWarranty(id);
        return "redirect:/technical/warranties";
    }

    @GetMapping("/view/{id}")
    public String view(Model model, @PathVariable int id, Authentication authentication) {
        WarrantyRepair warrantyRepair = null;
        Users users = (Users) authentication.getPrincipal();
        String roleName = users.getRole().getRoleName();

        if (roleName.equals("ADMIN")) {
            warrantyRepair = warrantyRepairService.getById(id);
        } else {
            warrantyRepair = warrantyRepairService.getById(id);
            if (warrantyRepair != null && warrantyRepair.getTechnical() != null) {
                int userId = warrantyRepair.getTechnical().getUserId();
                if (userId != users.getUserId()) {
                    return "redirect:/technical/warranties";
                }
            }
        }

        model.addAttribute("warrantyRepair", warrantyRepair);
        return "viewwarranty";
    }


    @PostMapping("/delete/{id}")
    public String deleteWarranty(@PathVariable int id) {
        WarrantyRepair wr = warrantyRepairService.getById(id);
        if (wr != null) {
            warrantyRepairService.deleteWarrantyRepair(id);
        }
        return "redirect:/technical/warranties";

    }
}
