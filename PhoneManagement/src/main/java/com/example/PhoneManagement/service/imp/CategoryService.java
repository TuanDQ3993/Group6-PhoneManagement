package com.example.PhoneManagement.service.imp;

import com.example.PhoneManagement.entity.Category;
import com.example.PhoneManagement.repository.CategoryRepository;
import com.example.PhoneManagement.repository.ColorRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.List;


public interface CategoryService {
    List<Category> findAllCategory();
}
