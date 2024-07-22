package com.example.PhoneManagement.service;

import com.example.PhoneManagement.entity.Colors;
import com.example.PhoneManagement.repository.ColorRepository;
import com.example.PhoneManagement.service.imp.ColorService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
public class ColorServiceImp implements ColorService {
    ColorRepository colorRepository;
    @Override
    public List<Colors> getAllColor(){
        return colorRepository.findAll();
    }
}
