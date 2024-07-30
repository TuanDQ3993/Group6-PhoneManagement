package com.example.PhoneManagement.service.imp;

import com.example.PhoneManagement.dto.request.PageDTO;
import com.example.PhoneManagement.entity.WarrantyRepair;
import lombok.Data;
import org.springframework.data.domain.Page;

import java.awt.print.Pageable;
import java.util.Date;
import java.util.List;

public interface WarrantyService {
    Page<WarrantyRepair> findAll(PageDTO pageable);
    Page<WarrantyRepair> findPaginated(PageDTO pageable, int id);

    boolean isExistRepairDate(Date date);

    public List<WarrantyRepair> getWarrantyByIdAndRepairDate(int technicalId, Date date);
    void addWarrantyRepair(WarrantyRepair wr);
    void updateWarrantyRepair(WarrantyRepair wr);
    void deleteWarrantyRepair(int id);


}
