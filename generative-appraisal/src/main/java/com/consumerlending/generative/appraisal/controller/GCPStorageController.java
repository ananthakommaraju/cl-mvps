package com.consumerlending.generative.appraisal.controller;

import com.consumerlending.generative.appraisal.service.GCPStorageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/storage")
public class GCPStorageController {

    private static final Logger logger = LoggerFactory.getLogger(GCPStorageController.class);
    private final GCPStorageService gcpStorageService;

    @Autowired
    public GCPStorageController(GCPStorageService gcpStorageService) {
        this.gcpStorageService = gcpStorageService;
    }

    @PostMapping("/upload")
    public ResponseEntity<Map<String, String>> uploadFile(@RequestParam("file") MultipartFile file, @RequestParam("bucketName") String bucketName) {
        Map<String, String> response = new HashMap<>();
        try {
            String fileUrl = gcpStorageService.uploadFile(bucketName, file.getName(), file);
            response.put("message", "File uploaded successfully");
            response.put("fileUrl", fileUrl);
            return ResponseEntity.ok(response);
        } catch (IOException e) {
            logger.error("Error uploading file", e);
            response.put("message", "Error uploading file");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }


}