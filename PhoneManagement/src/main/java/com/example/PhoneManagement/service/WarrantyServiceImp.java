package com.example.PhoneManagement.service;

import com.example.PhoneManagement.dto.request.PageDTO;
import com.example.PhoneManagement.entity.WarrantyRepair;
import com.example.PhoneManagement.repository.WarrantyRepairRepository;
import com.example.PhoneManagement.service.imp.WarrantyService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Date;
import java.util.List;
@Service
public class WarrantyServiceImp implements WarrantyService {
    @Autowired
    private WarrantyRepairRepository warrantyRepairRepository;

    @Override
    public Page<WarrantyRepair> findAll(PageDTO pageable) {
        try{
            int currentPage = pageable.getPageNumber();
            int pageSize = pageable.getPageSize();
            int startItem = currentPage * pageSize;
            List<WarrantyRepair> list = warrantyRepairRepository.findAll();
            List<WarrantyRepair> pageList = List.of();
            if(list.size() <startItem){
                list = Collections.emptyList();
            }else{
                int toIndex = Math.min(startItem + pageSize , list.size());
                pageList = list.subList(startItem,toIndex);
            }
            return new PageImpl<>(pageList, PageRequest.of(currentPage, pageSize), list.size());
        }catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Page<WarrantyRepair> findPaginated(PageDTO pageDTO, int id) {
        return warrantyRepairRepository.findPaginated(id, PageRequest.of(pageDTO.getPageNumber(), pageDTO.getPageSize()));
    }


    @Override
    @Transactional
    public void addWarrantyRepair(WarrantyRepair wr) {

    }

    @Override
    @Transactional
    public void updateWarrantyRepair(WarrantyRepair wr) {

    }

    @Override
    @Transactional
    public void deleteWarrantyRepair(int id) {

    }

    @Override
    public boolean isExistRepairDate(Date date) {
        return warrantyRepairRepository.existsWarrantyRepairsByRepairDate(date);
    }

    @Override
    public List<WarrantyRepair> getWarrantyByIdAndRepairDate(int technicalId, Date date) {
        return warrantyRepairRepository.findAllByTechnicalUserIdAndRepairDate(technicalId, date);
    }
}
