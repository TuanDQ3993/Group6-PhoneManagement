package com.example.PhoneManagement.service;

import com.example.PhoneManagement.dto.request.PageDTO;
import com.example.PhoneManagement.entity.Roles;
import com.example.PhoneManagement.repository.RoleRepository;
import com.example.PhoneManagement.service.imp.AccountService;

import com.example.PhoneManagement.entity.Users;
import com.example.PhoneManagement.repository.AccountRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.awt.print.Pageable;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

@Service
public class AccountServiceImp implements AccountService {
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

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
            List<Users> pagedList;

            if (list.size() < startItem) {
                pagedList = Collections.emptyList();
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

    @Override
    @Transactional
    public void banUser(int userId) {
        accountRepository.banUser(userId);
    }

    @Override
    @Transactional
    public void unBanUser(int userId) {
        accountRepository.unBanUser(userId);
    }

    @Override
    public Users getUserById(int userId) {
        return accountRepository.findById(userId).orElseThrow(() -> new RuntimeException("User Not Found"));
    }

//    @Override
//    public void editAccount(Users users) {
//
//    }

    @Override
    public boolean isPhoneExist(String phone) {
        return accountRepository.existsByPhoneNumber(phone);
    }

    @Override
    public boolean isEmailExist(String email) {
        return accountRepository.existsByUserName(email);
    }

    @Override
    public void createUser(Users user) {
        accountRepository.save(user);
    }



    @Override
    public Page<Users> searchAndFilterUsers(String query, PageDTO pageDTO, int role) {
        return accountRepository.findByRoleAndFullName(query, role, PageRequest.of(pageDTO.getPageNumber(), pageDTO.getPageSize()));
    }

    @Override
    public Page<Users> searchUsers(String query, PageDTO pageDTO) {
        return accountRepository.findByFullName(query, PageRequest.of(pageDTO.getPageNumber(), pageDTO.getPageSize()));
    }

    @Override
    public Page<Users> filterRole(int roleId, PageDTO pageDTO) {
        return accountRepository.findByRole(roleId, PageRequest.of(pageDTO.getPageNumber(), pageDTO.getPageSize()));
    }
    public boolean isValidEmail(String email) {
        String emailRegex = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$";
        return Pattern.matches(emailRegex, email);
    }

    public boolean isValidPhoneNumber(String phoneNumber) {
        String phoneRegex = "^[0-9]{10}$"; // Giả sử số điện thoại chỉ bao gồm 10 chữ số
        return Pattern.matches(phoneRegex, phoneNumber);
    }


    public void saveAll(List<Users> users) {
        accountRepository.saveAll(users);
    }

}
