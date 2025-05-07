package com.consumerlending.generative.appraisal.service;

import com.google.auth.Credentials;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import com.google.cloud.storage.StorageOptions;

@Service
public class GCPStorageService {

    private static final Logger logger = LoggerFactory.getLogger(GCPStorageService.class);

    @Value("${gcp.project.id}")
    private String gcpProjectId;

    @Value("${gcp.cloud.storage.root.bucket}")
    private String rootBucket;

    private Credentials credentials;

    @Autowired
    public GCPStorageService(Credentials credentials) {
        this.credentials = credentials;
    }

    public String uploadFile(String bucketName, String filePath, org.springframework.web.multipart.MultipartFile file) throws IOException {
        Storage storage = StorageOptions.newBuilder()
                .setProjectId(gcpProjectId)
                .setCredentials(credentials)
                .build()
                .getService();
        BlobId blobId = BlobId.of(rootBucket+"/"+bucketName, filePath + "/" + file.getOriginalFilename());        
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId).setContentType(file.getContentType()).build();
        byte[] fileContent = file.getBytes();
        storage.create(blobInfo, fileContent);

        logger.info("File {} uploaded to bucket {}/{}", file.getOriginalFilename(), bucketName, filePath);
        return "File " + file.getOriginalFilename() + " uploaded to bucket " + bucketName + "/" + filePath;
    }

    public byte[] downloadFile(String bucketName, String fullFilePath) throws IOException {
        Storage storage = StorageOptions.newBuilder()
                .setProjectId(gcpProjectId)
                .setCredentials(credentials)
                .build()
                .getService();
        com.google.cloud.storage.Blob blob = storage.get(BlobId.of(rootBucket+"/"+bucketName, fullFilePath));
        if (blob == null) {
            throw new IOException("File not found in bucket: "+rootBucket+"/"+ bucketName + "/" + fullFilePath);
        }
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        blob.downloadTo(outputStream);
        logger.info("File {} downloaded from bucket {}", fullFilePath, bucketName);
        return outputStream.toByteArray();
    }
}