package com.example.PhoneManagement.service.imp;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
public interface FileStorageService {
    public String storeFile(MultipartFile file);

    Path load(String filename);

    Resource loadAsResource(String filename);
}
