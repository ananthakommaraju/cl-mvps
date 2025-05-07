package com.consumerlending.generative.appraisal.function;

import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.auth.Credentials;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.documentai.v1.Document;
import com.google.cloud.documentai.v1.DocumentProcessorServiceClient;
import com.google.cloud.documentai.v1.DocumentProcessorServiceSettings;
import com.google.cloud.documentai.v1.ProcessRequest;
import com.google.cloud.documentai.v1.ProcessResponse;
import com.google.cloud.documentai.v1.RawDocument;
import com.google.cloud.functions.CloudEventsFunction;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import io.cloudevents.CloudEvent;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;
import java.nio.charset.StandardCharsets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DocumentSummaryFunction implements CloudEventsFunction {

    private static final Logger logger = LoggerFactory.getLogger(DocumentSummaryFunction.class);
    private static final String PROJECT_ID = System.getenv("GCP_PROJECT");
    private static final String LOCATION = "europe-west2";
    private static final String PROCESSOR_ID = System.getenv("PROCESSOR_ID"); // Replace with your processor ID

    @Override
    public void accept(CloudEvent cloudEvent) throws Exception {
        Credentials credentials = GoogleCredentials.getApplicationDefault();
        Map<String, String> eventData = parseEvent(cloudEvent);
        String bucketName = eventData.get("bucket");
        String fileName = eventData.get("name");
        String eventType = cloudEvent.getType();

        try {
            logger.info("Document Summary Function triggered by event: " + eventType);
            logger.info("Processing file: " + fileName + " in bucket: " + bucketName);

            if (PROJECT_ID == null || PROCESSOR_ID == null) {
                throw new IllegalStateException("GCP_PROJECT or PROCESSOR_ID environment variable is not set.");
            }

            // Initialize Storage client
            Storage storage = StorageOptions.newBuilder().setCredentials(credentials)
                    .setProjectId(PROJECT_ID).build().getService();

            // Get the file from Cloud Storage
            BlobId blobId = BlobId.of(bucketName, fileName);
            Blob blob = storage.get(blobId);
            if (blob == null) {
                throw new IOException("File not found: " + fileName + " in bucket: " + bucketName);
            }

            // Read the file contents
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            blob.downloadTo(out);
            byte[] documentContent = out.toByteArray();

            // Initialize Document AI client
            String endpoint = String.format("%s-documentai.googleapis.com:443", LOCATION);

            DocumentProcessorServiceSettings settings = DocumentProcessorServiceSettings.newBuilder()
                    .setEndpoint(endpoint)
                    .setCredentialsProvider(FixedCredentialsProvider.create(credentials))
                    .build();

            try (DocumentProcessorServiceClient client = DocumentProcessorServiceClient.create(settings)) {
                // Configure the process request.
                RawDocument rawDocument = RawDocument.newBuilder()
                        .setContent(ByteString.copyFrom(documentContent))
                        .setMimeType(blob.getContentType())
                        .build();

                String processorName = String.format("projects/%s/locations/%s/processors/%s", PROJECT_ID, LOCATION,
                        PROCESSOR_ID);
                ProcessRequest processRequest = ProcessRequest.newBuilder()
                        .setName(processorName)
                        .setRawDocument(rawDocument)
                        .build();

                // Process the document
                ProcessResponse processResponse = client.processDocument(processRequest);
                Document document = processResponse.getDocument();

                if (document != null) {
                    String summary = document.getText();
                    logger.info("Summary : " + summary);
                    logger.info("Document processed successfully: " + fileName);
                } else {
                    throw new RuntimeException("Document AI did not return a document.");
                }
            }
        } catch (Exception e) {
            logger.error("Error processing document: {}", fileName, e);
        }
    }

    private Map<String, String> parseEvent(CloudEvent cloudEvent) throws InvalidProtocolBufferException {
        Map<String, String> eventData = null;

        if (cloudEvent.getData() != null) {
            String jsonString = new String(cloudEvent.getData().toBytes(), StandardCharsets.UTF_8);
            try {
                eventData = com.google.gson.JsonParser.parseString(jsonString).getAsJsonObject().asMap().entrySet()
                        .stream().collect(java.util.LinkedHashMap::new,
                                (m, v) -> m.put(v.getKey(), v.getValue().getAsString()),
                                java.util.LinkedHashMap::putAll);
            } catch (Exception e) {
                logger.error("Error parsing event data: ", e);
            }
        }
        if (eventData == null) {
            throw new RuntimeException("Error mapping data: " + cloudEvent.getData());
        }

        return eventData;
    }
}