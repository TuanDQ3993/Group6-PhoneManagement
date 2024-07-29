package com.example.PhoneManagement.service;

import com.example.PhoneManagement.dto.request.ColorDTO;
import com.example.PhoneManagement.entity.Colors;
import com.example.PhoneManagement.repository.ColorRepository;
import com.example.PhoneManagement.service.imp.ColorService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
public class ColorServiceImp implements ColorService {
    ColorRepository colorRepository;
    @Override
    public List<ColorDTO> getAllColor(){
        List<Colors> colors= colorRepository.findAll();
        List<ColorDTO> colorDTOS=new ArrayList<>();
        for(Colors color:colors){
            ColorDTO colorDTO=new ColorDTO();
            colorDTO.setColorId(color.getColorId());
            colorDTO.setColorName(color.getColorName());
            colorDTOS.add(colorDTO);
        }
        return colorDTOS;
    }
}
