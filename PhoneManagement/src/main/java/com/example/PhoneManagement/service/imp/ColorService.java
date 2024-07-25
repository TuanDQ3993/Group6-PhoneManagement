package com.example.PhoneManagement.service.imp;

import com.example.PhoneManagement.dto.request.ColorDTO;
import com.example.PhoneManagement.entity.Colors;
import com.example.PhoneManagement.repository.ColorRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.List;



public interface ColorService {
     List<ColorDTO> getAllColor();

}
