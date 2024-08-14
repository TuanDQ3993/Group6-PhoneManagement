package com.example.PhoneManagement.service;

import com.example.PhoneManagement.dto.request.ColorDTO;
import com.example.PhoneManagement.entity.Colors;
import com.example.PhoneManagement.repository.ColorRepository;
import com.example.PhoneManagement.service.imp.ColorService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

class ColorServiceImpTest {

    @InjectMocks
    private ColorServiceImp colorService;

    @Mock
    private ColorRepository colorRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllColor() {

        Colors color1 = new Colors();
        color1.setColorId(1);
        color1.setColorName("Red");

        Colors color2 = new Colors();
        color2.setColorId(2);
        color2.setColorName("Blue");

        List<Colors> colors = Arrays.asList(color1, color2);
        when(colorRepository.findAll()).thenReturn(colors);


        List<ColorDTO> result = colorService.getAllColor();

        assertEquals(2, result.size());
        assertEquals(1, result.get(0).getColorId());
        assertEquals("Red", result.get(0).getColorName());
        assertEquals(2, result.get(1).getColorId());
        assertEquals("Blue", result.get(1).getColorName());
    }
}
