package com.example.PhoneManagement.service;

import com.example.PhoneManagement.entity.Purchase;
import com.example.PhoneManagement.repository.PurchaseDetailsRepository;
import com.example.PhoneManagement.repository.PurchaseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.jxls.common.Context;
import org.jxls.util.JxlsHelper;
import java.io.InputStream;
import java.io.OutputStream;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class DashboardService {
    @Autowired
    private PurchaseRepository purchaseRepository;

    @Autowired
    private PurchaseDetailsRepository purchaseDetailRepository;

    public List<Object[]> getDailyPurchasesForUser(int userId) {
        return purchaseRepository.findDailyPurchasesForUser(userId);
    }

    public List<Object[]> getDailyProductsForUser(int userId) {
        return purchaseDetailRepository.findDailyProductsForUser(userId);
    }

    public List<Object[]> getFilteredDailyPurchasesForUser(int userId, LocalDate startDate, LocalDate endDate, String status) {
        if (startDate == null && endDate == null && (status == null || status.isEmpty())) {
            return purchaseRepository.findDailyPurchasesForUser(userId);
        } else {
            return purchaseRepository.findFilteredDailyPurchasesForUser(userId, startDate, endDate, status);
        }
    }

    public List<Object[]> getDailyPurchaseProduct(int userId) {
        return purchaseDetailRepository.findDailyPurchaseProduct(userId);
    }

    public void exportDataToExcelTemplate(OutputStream outputStream) {
        List<Purchase> data = initializeData();

        Map<String, Object> doUongs = new HashMap<>();
        doUongs.put("data", data);

        try (InputStream inputStream = this.getClass().getResourceAsStream("/template_exports/template_server_list_for_export2.xlsx")) {
            if (inputStream == null) {
                throw new RuntimeException("Template not found in resources/template_exports");
            }
            if (inputStream.available() == 0) {
                throw new RuntimeException("The supplied file was empty (zero bytes long)");
            }
            System.out.println("Template found and is not empty");
            Context context = new Context();
            context.toMap().putAll(doUongs);
            JxlsHelper.getInstance().processTemplate(inputStream, outputStream, context);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private List<Purchase> initializeData() {
        try {
            List<Purchase> purchases =purchaseRepository.findAll();
            return purchases;
        }catch (Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }
}
