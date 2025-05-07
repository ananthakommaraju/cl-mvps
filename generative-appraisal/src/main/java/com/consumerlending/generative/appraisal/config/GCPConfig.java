package com.consumerlending.generative.appraisal.config;

import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.auth.Credentials;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.auth.oauth2.ImpersonatedCredentials;
import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.cloud.storage.*;
import com.google.api.gax.rpc.NotFoundException;
import com.google.pubsub.v1.ProjectName;

import com.google.cloud.pubsub.v1.TopicAdminSettings;
import com.google.pubsub.v1.Topic;
import jakarta.annotation.PostConstruct;

import java.io.IOException;
import java.util.*;

import com.google.cloud.documentai.v1.DocumentProcessorServiceClient;
import com.google.cloud.pubsub.v1.TopicAdminClient;
import com.google.cloud.documentai.v1.DocumentProcessorServiceSettings;
import com.google.cloud.storage.BucketInfo;
import com.google.cloud.storage.Storage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

@Configuration
public class GCPConfig {

    private static final Logger logger = LoggerFactory.getLogger(GCPConfig.class);
    private TopicAdminClient topicAdminClient;

    private Storage storage;
    private List<String> bucketNames = Arrays.asList("monthly-status", "bi-weekly-status");

    @Value("${gcp.service.account}")
    private String serviceAccount;

    @Value("${gcp.service.account.impersonated}")
    private String saEmail;

    public GCPConfig() {
    }

    @Value("${gcp.project.id}")
    private String projectId;

    @Bean
    public Storage storage() throws IOException {
        return StorageOptions.newBuilder()
                .setCredentials(getCredentials())
                .setProjectId(projectId)
                .build().getService();
    }

    @Bean
    public DocumentProcessorServiceClient documentProcessorServiceClient() throws IOException {
        DocumentProcessorServiceSettings settings = DocumentProcessorServiceSettings.newBuilder()
                .setCredentialsProvider(FixedCredentialsProvider.create(getCredentials()))
                .build();
        return DocumentProcessorServiceClient.create(settings);
    }

    @Bean
    public Credentials getCredentials() throws IOException {
        return impersonateServiceAccount();
    }

    public Credentials impersonateServiceAccount() throws IOException {
        GoogleCredentials credentials = ServiceAccountCredentials.newBuilder().setClientEmail(saEmail).build();
        
        credentials.createScoped(Collections.singletonList("https://www.googleapis.com/auth/cloud-platform"));

        ImpersonatedCredentials impersonatedCredentials = 
            ImpersonatedCredentials.newBuilder().setSourceCredentials(credentials)
            .setTargetPrincipal(serviceAccount)
            .setScopes(Collections.singletonList("https://www.googleapis.com/auth/cloud-platform"))
            .setLifetime(3600)
            .build();

        return FixedCredentialsProvider.create(impersonatedCredentials).getCredentials();
    }

    public String getServiceAccountName() {
        return serviceAccount;
    }

    @Value("${gcp.service.account.impersonated}")
    private String serviceAccountName;

    @Value("${gcp.pubsub.topic}")
    private String topicName;

    @PostConstruct
    public void setTopicAdminClient() {
        try {

            TopicAdminSettings topicAdminSettings = TopicAdminSettings.newBuilder()
                    .build();
            topicAdminClient = TopicAdminClient.create(topicAdminSettings);
        } catch (Exception e) {
            logger.error("Error", e);
        }
    }
    public List<Topic> listTopics(String projectId) {
        try {
            ProjectName projectName = ProjectName.of(projectId);
            List<Topic> topicList = new ArrayList<>();
            TopicAdminClient.ListTopicsPagedResponse response = topicAdminClient.listTopics(projectName);
            for(Topic topic : response.iterateAll()){
                topicList.add(topic);
            }
            return topicList;
        } catch (Exception e) {
            logger.error("Error while creating topics", e);
        }
        return null;
    }

    @PostConstruct
    public void init() throws IOException {
        createBuckets(bucketNames);
        configureBucketNotifications();
        createPubSubTopic();
    }

    public void createPubSubTopic() {

        try {
            List<Topic> topics = listTopics(projectId);

            Optional<Topic> existingTopic = topics.stream().filter(t -> t.getName().endsWith(topicName))
                    .findFirst();

            if (existingTopic.isEmpty()) {
                Topic newTopic = Topic.newBuilder().setName(getTopicName()).build();
                topicAdminClient.createTopic(newTopic);
                logger.info("Topic {} created.", topicName);
            } else {
                logger.info("Topic {} already exists.", topicName);
            }
        } catch (NotFoundException e) {
            logger.error("Error while creating topics", e);
        }
    }

    public void createBuckets(List<String> bucketNames) throws IOException {
        for (String bucketName : bucketNames) {
            storage = storage();
            try {

                Bucket bucket = storage.get(bucketName);
                if (bucket == null) {
                    storage.create(BucketInfo.newBuilder(bucketName).setStorageClass(StorageClass.STANDARD)
                            .setLocation("US").build());
                    logger.info("Bucket {} created.", bucketName);
                } else {
                    logger.info("Bucket {} already exists.", bucketName);
                }
            } catch (Exception e) {
                logger.error("Error creating bucket: " + bucketName, e);
            }
        }
    }

    public void configureBucketNotifications() {
        bucketNames.forEach(bucketName -> {
            try {
                storage = storage();
                String notificationTopic = "projects/" + projectId + "/topics/" + topicName;

                Iterable<Notification> notifications = storage.listNotifications(bucketName);
                boolean notificationExists = false;

                if (notifications != null) {
                    notificationExists = java.util.stream.StreamSupport.stream(notifications.spliterator(), false)
                            .anyMatch(notification -> notification.getTopic().equals(notificationTopic));

                }
                if (!notificationExists) {
                    Map<String, String> customAttributes = new java.util.HashMap<>();

                    customAttributes.put("bucketName", bucketName);
                    NotificationInfo notificationInfo = NotificationInfo.newBuilder(notificationTopic)
                            .setCustomAttributes(customAttributes)
                            .setEventTypes(NotificationInfo.EventType.OBJECT_FINALIZE)
                            .setPayloadFormat(NotificationInfo.PayloadFormat.JSON_API_V1).build();
                    storage.createNotification(bucketName, notificationInfo);
                    logger.info("Notification for bucket {} created.", bucketName);
                } else {
                    logger.info("Notification for bucket {} already exists.", bucketName);
                }
            } catch (Exception e) {
                logger.error("Error configuring notification for bucket: " + bucketName, e);
            }
        });
    }

    private String getTopicName() {
        return "projects/" + projectId + "/topics/" + topicName;
    }
}