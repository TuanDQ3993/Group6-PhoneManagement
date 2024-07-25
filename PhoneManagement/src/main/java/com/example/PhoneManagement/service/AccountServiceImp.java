package com.example.PhoneManagement.service;

import com.example.PhoneManagement.dto.request.PageDTO;
import com.example.PhoneManagement.service.imp.AccountService;

import com.example.PhoneManagement.entity.Users;
import com.example.PhoneManagement.repository.AccountRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class AccountServiceImp implements AccountService {
    @Autowired
    private AccountRepository accountRepository;


    // Get list of all account
    @Override
    public List<Users> getAll() {
        return accountRepository.findAllOrderByCreatedAt();
    }

    // Get list of account with paging
    @Override
    public Page<Users> findPaginated(PageDTO pageable) {
        try {
            int currentPage = pageable.getPageNumber();
            int pageSize = pageable.getPageSize();
            int startItem = currentPage * pageSize;
            List<Users> list = accountRepository.findAllOrderByCreatedAt();
            List<Users> pagedList = List.of();
            if (list.size() < startItem) {
                list = Collections.emptyList();
            } else {
                int toIndex = Math.min(startItem + pageSize, list.size());
                pagedList = list.subList(startItem, toIndex);
            }
            return new PageImpl<>(pagedList, PageRequest.of(currentPage, pageSize), list.size());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // Ban account user
    @Override
    @Transactional
    public void banUser(int userId) {
        accountRepository.banUser(userId);
    }

    //Unban account user
    @Override
    @Transactional
    public void unBanUser(int userId) {
        accountRepository.unBanUser(userId);
    }

    // Get single user
    @Override
    public Users getUserById(int userId) {
        return accountRepository.findById(userId).orElseThrow(() -> new RuntimeException("User Not Found"));
    }

    // Search account by name
    @Override
    public List<Users> searchUsers(String text) {
        return accountRepository.searchUsersRepo(text);
    }



}
