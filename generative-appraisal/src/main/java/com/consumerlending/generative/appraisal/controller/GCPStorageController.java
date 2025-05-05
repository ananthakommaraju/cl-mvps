package com.consumerlending.generative.appraisal.controller;

import com.consumerlending.generative.appraisal.service.GCPStorageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.google.cloud.storage.BlobInfo;



@RestController
@RequestMapping("/api/storage")
public class GCPStorageController {

    private static final Logger logger = LoggerFactory.getLogger(GCPStorageController.class);
    private final GCPStorageService gcpStorageService;

    @Autowired
    public GCPStorageController(GCPStorageService gcpStorageService) {
        this.gcpStorageService = gcpStorageService;
    }


    @PostMapping("/event")
    public ResponseEntity<String> handleFileUploadedEvent(@RequestBody BlobInfo blobInfo) {
        try {
             gcpStorageService.handleFileUploadedEvent(blobInfo);
            return ResponseEntity.ok("File event handled successfully.");
         } catch (Exception e) {
            logger.error("Error handling file uploaded event", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error handling file event");
        }
    }
}