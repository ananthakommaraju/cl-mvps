package com.consumerlending.generative.appraisal.config;

import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.auth.Credentials;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.auth.oauth2.ImpersonatedCredentials;
import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.cloud.pubsub.v1.TopicAdminClient;
import com.google.cloud.pubsub.v1.TopicAdminSettings;
import com.google.cloud.storage.*;
import com.google.pubsub.v1.Topic;
import com.google.pubsub.v1.TopicName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class GCPConfig {

    private static final Logger logger = LoggerFactory.getLogger(GCPConfig.class);

    @Value("${gcp.project.id}")
    private String gcpProjectId;

    @Value("${gcp.service.account}")
    private String gcpServiceAccount;

    @Value("${gcp.service.account.impersonated}")
    private String saEmail;

    @Value("${gcp.cloud.storage.root.bucket}")
    private String gcpBucket;

    @Value("${gcp.topic.name}")
    private String gcpTopic;

    @Bean
    public Credentials getCredentials() throws IOException {
        return impersonateServiceAccount();
    }

    @Bean
    public Storage storage() throws IOException {
        try {
            logger.info("Creating storage object with :{}", gcpServiceAccount);
            Credentials credentials = GoogleCredentials.fromStream(new ByteArrayInputStream(gcpServiceAccount.getBytes()));
            StorageOptions storageOptions = StorageOptions.newBuilder()
                    .setProjectId(gcpProjectId)
                    .setCredentials(credentials)
                    .build();
            Storage storage = storageOptions.getService();
            createBuckets(storage);
            configureBucketNotifications(storage);
            return storage;
        } catch (Exception e) {
            logger.error("Could not create Storage object with :{}, error: {}", gcpServiceAccount, e.getMessage());
            throw e;
        }
    }

    @Bean
    public TopicAdminClient topicAdminClient() throws IOException {
        logger.info("Creating topic object with :{}", gcpServiceAccount);
        Credentials credentials = GoogleCredentials.fromStream(new ByteArrayInputStream(gcpServiceAccount.getBytes()));
        TopicAdminSettings topicAdminSettings = TopicAdminSettings.newBuilder()
                .setCredentialsProvider(FixedCredentialsProvider.create(credentials))
                .build();
        TopicAdminClient topicAdminClient = TopicAdminClient.create(topicAdminSettings);
        createPubSubTopic(topicAdminClient);
        return topicAdminClient;
    }

    private Credentials impersonateServiceAccount() throws IOException {
        GoogleCredentials credentials = ServiceAccountCredentials.newBuilder().setClientEmail(saEmail).build();

        credentials.createScoped(Collections.singletonList("https://www.googleapis.com/auth/cloud-platform"));

        ImpersonatedCredentials impersonatedCredentials =
                ImpersonatedCredentials.newBuilder().setSourceCredentials(credentials)
                        .setTargetPrincipal(gcpServiceAccount)
                        .setScopes(Collections.singletonList("https://www.googleapis.com/auth/cloud-platform"))
                        .setLifetime(3600)
                        .build();

        return FixedCredentialsProvider.create(impersonatedCredentials).getCredentials();
    }

    private void createBuckets(Storage storage) {
        try {
            if (!storage.get(gcpBucket).exists()) {
                logger.info("Creating Bucket :{} ", gcpBucket);
                Bucket bucket = storage.create(BucketInfo.of(gcpBucket));
                logger.info("Bucket {} created.", bucket.getName());
            } else {
                logger.info("Bucket {} already exists.", gcpBucket);
            }
        } catch (StorageException e) {
            logger.error("Failed to create bucket: {}", e.getMessage());
        }
    }

    private void createPubSubTopic(TopicAdminClient topicAdminClient) {
        try {
            String projectTopicName = String.format("projects/%s/topics/%s", gcpProjectId, gcpTopic);
            TopicName topicName = TopicName.parse(projectTopicName);
            if (topicAdminClient.getTopic(topicName) == null) {
                logger.info("Creating PubSub Topic :{}", gcpTopic);
                Topic topic = topicAdminClient.createTopic(topicName);
                logger.info("Topic {} created.", topic.getName());
            } else {
                logger.info("Topic {} already exists.", gcpTopic);
            }
        } catch (Exception e) {
            logger.error("Failed to create PubSub topic: {}", e.getMessage());
        }
    }

    private void configureBucketNotifications(Storage storage) {
        try {
            logger.info("Configuring Bucket Notification : {}", gcpBucket);
            String projectTopicName = String.format("projects/%s/topics/%s", gcpProjectId, gcpTopic);

            Map<String, String> customAttributes = new HashMap<>();
            customAttributes.put("notificationType", "APPRAISAL_NOTIFICATION");

            Bucket bucket = storage.get(gcpBucket);
            // Check if the notification exists based on topic name and type
            boolean notificationExists = storage.listNotifications(bucket.getName())
                .stream().anyMatch(notification -> notification.getTopic().equals(projectTopicName));

            if (!notificationExists) {
                logger.info("Creating Notification in Bucket:{} ", gcpBucket);
                NotificationInfo notificationInfo = NotificationInfo.newBuilder(TopicName.parse(projectTopicName).getTopic())
                        .setCustomAttributes(customAttributes)
                        .setEventTypes(NotificationInfo.EventType.OBJECT_FINALIZE)
                        .build();
                storage.createNotification(bucket.getName(), notificationInfo);
            } else {
                logger.info("Notification already exists in Bucket:{} ", gcpBucket);
            }
        } catch (Exception e) {
            logger.error("Failed to configure bucket notification: {}", e.getMessage());
        }
    }
}
