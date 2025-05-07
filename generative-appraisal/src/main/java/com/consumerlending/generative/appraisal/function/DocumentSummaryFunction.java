package com.consumerlending.generative.appraisal.function;

import com.google.cloud.functions.CloudEvent;
import com.google.cloud.functions.StorageEvent;
import com.google.cloud.functions.StorageFunction;
import com.google.cloud.documentai.v1.Document;
import com.google.cloud.documentai.v1.DocumentProcessorServiceClient;
import com.google.cloud.documentai.v1.DocumentProcessorServiceSettings;
import com.google.cloud.documentai.v1.ProcessRequest;
import com.google.cloud.documentai.v1.ProcessResponse;
import com.google.cloud.documentai.v1.RawDocument;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import com.google.cloud.logging.LogEntry;
import com.google.cloud.logging.Logging;
import com.google.cloud.logging.LoggingOptions;
import com.google.cloud.logging.Payload;
import com.google.protobuf.ByteString;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DocumentSummaryFunction implements StorageFunction {

    private static final Logger logger = Logger.getLogger(DocumentSummaryFunction.class.getName());
    private static final String PROJECT_ID = System.getenv("GCP_PROJECT");
    private static final String LOCATION = "us"; // e.g., "us", "eu"
    private static final String PROCESSOR_ID = System.getenv("PROCESSOR_ID"); // Replace with your processor ID

    @Override
    public void accept(StorageEvent event, CloudEvent cloudEvent) throws IOException {
        String bucketName = event.getBucket();
        String fileName = event.getName();
        String eventType = cloudEvent.getType();

        try (Logging logging = LoggingOptions.getDefaultInstance().getService()) {

            LogEntry entry = LogEntry.newBuilder(Payload.StringPayload.of("Document Summary Function triggered by event: " + eventType))
                    .setLogName("document-summary-function-log").build();
            logging.write(java.util.Collections.singleton(entry));

            logger.info("Processing file: " + fileName + " in bucket: " + bucketName);

            if (PROJECT_ID == null || PROCESSOR_ID == null) {
                throw new IllegalStateException("GCP_PROJECT or PROCESSOR_ID environment variable is not set.");
            }

            // Initialize Storage client
            Storage storage = StorageOptions.newBuilder().setProjectId(PROJECT_ID).build().getService();

            // Get the file from Cloud Storage
            BlobId blobId = BlobId.of(bucketName, fileName);
            Blob blob = storage.get(blobId);
            if (blob == null || !blob.exists()) {
                throw new IOException("File not found: " + fileName + " in bucket: " + bucketName);
            }

            // Read the file contents
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            blob.downloadTo(out);
            byte[] documentContent = out.toByteArray();

            // Initialize Document AI client
            String endpoint = String.format("%s-documentai.googleapis.com:443", LOCATION);

            DocumentProcessorServiceSettings settings =
                    DocumentProcessorServiceSettings.newBuilder().setEndpoint(endpoint).build();
            try (DocumentProcessorServiceClient client = DocumentProcessorServiceClient.create(settings)) {
                // Configure the process request
                RawDocument rawDocument = RawDocument.newBuilder()
                        .setContent(ByteString.copyFrom(documentContent))
                        .setMimeType(blob.getContentType())
                        .build();

                String processorName = String.format("projects/%s/locations/%s/processors/%s", PROJECT_ID, LOCATION, PROCESSOR_ID);
                ProcessRequest processRequest = ProcessRequest.newBuilder()
                        .setName(processorName)
                        .setRawDocument(rawDocument)
                        .build();

                // Process the document
                ProcessResponse processResponse = client.processDocument(processRequest);
                Document document = processResponse.getDocument();

                if (document != null) {
                    String summary = document.getText();
                    LogEntry summeryEntry = LogEntry.newBuilder(Payload.StringPayload.of("Summary of " + fileName + " : " + summary))
                            .setLogName("document-summary-function-log").build();
                    logging.write(java.util.Collections.singleton(summeryEntry));

                    logger.info("Summary : " + summary);
                    logger.info("Document processed successfully: " + fileName);

                } else {
                    throw new RuntimeException("Document AI did not return a document.");
                }

            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error processing document: " + fileName, e);
            try (Logging logging = LoggingOptions.getDefaultInstance().getService()) {
                LogEntry errorEntry = LogEntry.newBuilder(Payload.StringPayload.of("Error processing document " + fileName + ": " + e.getMessage()))
                        .setLogName("document-summary-function-log").build();
                logging.write(java.util.Collections.singleton(errorEntry));
            }
        }
    }
}