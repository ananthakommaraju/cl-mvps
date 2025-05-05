package com.consumerlending.generative.appraisal.controller;

import com.consumerlending.generative.appraisal.dto.StatusReportRequest;
import com.consumerlending.generative.appraisal.service.GCPStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/status-report")
public class StatusReportController {

    private final GCPStorageService gcpStorageService;

    @Autowired
    public StatusReportController(GCPStorageService gcpStorageService) {
        this.gcpStorageService = gcpStorageService;
    }

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> uploadStatusReport(
            @RequestPart("file") MultipartFile file,
            @RequestPart("request") StatusReportRequest request) {
        try {
            gcpStorageService.uploadFile(request.getBucketName(), request.getFilePath(), file);
            return ResponseEntity.ok("File uploaded successfully to bucket: " + request.getBucketName());
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to upload file: " + e.getMessage());
        }
    }

    @PostMapping("/download")
    public ResponseEntity<byte[]> downloadStatusReport(@RequestBody StatusReportRequest request) {
        try {
            byte[] fileContent = gcpStorageService.downloadFile(request.getBucketName(), request.getFilePath());
            return ResponseEntity.ok()
                    .header("Content-Disposition", "attachment; filename=\"" + request.getFileName() + "\"")
                    .body(fileContent);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}
