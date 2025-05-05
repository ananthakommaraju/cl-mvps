package com.consumerlending.generative.appraisal.service;

import com.consumerlending.generative.appraisal.dto.StatusReportRequest;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Service
public class GCPStorageService {

    private static final Logger logger = LoggerFactory.getLogger(GCPStorageService.class);

    @Value("${gcp.project.id}")
    private String gcpProjectId;

    private final SummaryReportService summaryReportService;

    @Autowired
    public GCPStorageService(SummaryReportService summaryReportService) {
        this.summaryReportService = summaryReportService;
    }


   public String uploadFile(String bucketName, String filePath, MultipartFile file) throws IOException {
        Storage storage = StorageOptions.newBuilder().setProjectId(gcpProjectId).build().getService();

        BlobId blobId = BlobId.of(bucketName, filePath + "/" + file.getOriginalFilename());
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId).setContentType(file.getContentType()).build();
        byte[] fileContent = file.getBytes();
        storage.create(blobInfo, fileContent);

        logger.info("File {} uploaded to bucket {}/{}", file.getOriginalFilename(), bucketName, filePath);
        return "File " + file.getOriginalFilename() + " uploaded to bucket " + bucketName + "/" + filePath;
    }

    public byte[] downloadFile(String bucketName, String fullFilePath) throws IOException {
        Storage storage = StorageOptions.newBuilder().setProjectId(gcpProjectId).build().getService();
        String fileName = fullFilePath.substring(fullFilePath.lastIndexOf("/") + 1);
        String filePath = fullFilePath.substring(0, fullFilePath.lastIndexOf("/"));
        Blob blob = storage.get(BlobId.of(bucketName, fullFilePath));
        if (blob == null) {
            throw new IOException("File not found in bucket: " + bucketName + "/" + filePath + "/" + fileName);
        }
        
        
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        blob.downloadTo(outputStream);
        logger.info("File {} downloaded from bucket {}/{}", fileName, bucketName, filePath);
        return outputStream.toByteArray();
    }

    public void sendFileToAnotherService(String bucketName, String filePath, String fileName) throws IOException {
        String fullFilePath = filePath +"/"+ fileName;
        byte[] fileContent = downloadFile(bucketName, fullFilePath);
        StatusReportRequest statusReportRequest = new StatusReportRequest();
        statusReportRequest.setBucketName(bucketName);
        statusReportRequest.setFilePath(filePath);
        statusReportRequest.setFileName(fileName);
        statusReportRequest.setFileContent(fileContent);

        logger.info("Sending file {} from bucket {}/{} to SummaryReportService", fileName, bucketName, filePath);
        summaryReportService.generateReportFromPPT(statusReportRequest);
        logger.info("File {} sent to SummaryReportService", fileName);
    }

    public void handleFileUploadedEvent(BlobInfo blobInfo) {
        try {
            String bucketName = blobInfo.getBucket();
            String fullFilePath = blobInfo.getName();
            String fileName = fullFilePath.substring(fullFilePath.lastIndexOf("/") + 1);
            String filePath = fullFilePath.substring(0,fullFilePath.lastIndexOf("/"));
            logger.info("File uploaded event detected for file: {} in bucket: {}/{}", fileName, bucketName, filePath);
            sendFileToAnotherService(bucketName, filePath, fileName);
        } catch (IOException e) {
            logger.error("Error handling file uploaded event", e);
        }
    }
}