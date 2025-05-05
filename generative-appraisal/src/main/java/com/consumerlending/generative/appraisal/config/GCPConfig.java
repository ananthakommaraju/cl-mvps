package com.consumerlending.generative.appraisal.config;

import com.google.cloud.pubsub.v1.TopicAdminClient;
import com.google.cloud.pubsub.v1.TopicIterator;
import com.google.cloud.storage.Bucket;
import com.google.cloud.storage.BucketInfo;
import com.google.cloud.storage.Notification;
import com.google.cloud.storage.NotificationInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageClass;
import com.google.cloud.storage.StorageOptions;
import com.google.pubsub.v1.Topic;
import com.google.pubsub.v1.TopicName;
import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import com.google.cloud.pubsub.v1.stub.GrpcTopicAdminStub;
import com.google.cloud.pubsub.v1.stub.TopicAdminStub;
import com.google.cloud.pubsub.v1.stub.TopicAdminStubSettings;
import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.pubsub.v1.PubSubAdminClient;

@Configuration
public class GCPConfig {

    private static final Logger logger = LoggerFactory.getLogger(GCPConfig.class);
    private Storage storage;
    private PubSubAdminClient client;
    private List<String> bucketNames = Arrays.asList("monthly-status", "bi-weekly-status");

    @Value("${gcp.project.id}")
    private String projectId;

    @Value("${gcp.pubsub.topic}")
    private String topicName;
    
    @PostConstruct
    public void init() {
        try {
            createPubSubTopic();
            createBuckets();
            configureBucketNotifications();
        } catch (IOException e) {
            logger.error("Error initializing GCP resources", e);
        }
    }
    
    @PostConstruct
    public void initGCPClients() {
      try {
        // Initialize Storage client
        storage = StorageOptions.newBuilder().setProjectId(projectId).build().getService();
        
        // Initialize Pub/Sub Admin client
        GoogleCredentials credentials = GoogleCredentials.getApplicationDefault();
        TopicAdminStub topicAdminStub = GrpcTopicAdminStub.create(TopicAdminStubSettings.newBuilder()
                .setCredentialsProvider(FixedCredentialsProvider.create(credentials)).build());
        client = PubSubAdminClient.create(topicAdminStub);
      } catch (Exception e) {
        logger.error("Error initializing GCP clients", e);
      }
    }
    
    public void createPubSubTopic() throws IOException {
        TopicName topic = TopicName.of(projectId, topicName);
        TopicIterator topics = client.listTopics(com.google.pubsub.v1.ProjectName.of(projectId)).iterateAll();
        boolean topicExists = false;
        
        while(topics.hasNext()){
           if(topics.next().getName().equals(topic.toString())){
              topicExists = true;
              break;
           }
        }
        
        if (!topicExists) {
            client.createTopic(topic);
            logger.info("Topic {} created.", topicName);
        } else {
            logger.info("Topic {} already exists.", topicName);
        }
    }

    public void createBuckets() {
        bucketNames.forEach(bucketName -> {
            try {
                Bucket bucket = storage.get(bucketName);
                if (bucket == null) {
                    storage.create(BucketInfo.newBuilder(bucketName).setStorageClass(StorageClass.STANDARD).setLocation("US").build());
                    logger.info("Bucket {} created.", bucketName);
                } else {
                    logger.info("Bucket {} already exists.", bucketName);
                }
            } catch (Exception e) {
                logger.error("Error creating bucket: " + bucketName, e);
            }
        });
    }

    public void configureBucketNotifications() {
        
        bucketNames.forEach(bucketName -> {
            try {
              Bucket bucket = storage.get(bucketName);
              String notificationTopic = "projects/" + projectId + "/topics/" + topicName;
              List<Notification> notifications = bucket.listNotifications();
              boolean notificationExists = false;
              for(Notification notification : notifications){
                 if(notification.getTopic().equals(notificationTopic)){
                    notificationExists = true;
                 }
              }
              if (!notificationExists) {
                 Map<String, String> customAttributes = new HashMap<>();
                 customAttributes.put("bucketName", bucketName);
                  NotificationInfo notificationInfo = NotificationInfo.newBuilder(notificationTopic).setCustomAttributes(customAttributes)
                  .setEventTypes(List.of(NotificationInfo.EventType.OBJECT_FINALIZE)).setPayloadFormat(NotificationInfo.PayloadFormat.JSON_API_V1).build();
                  bucket.createNotification(notificationInfo);
                  logger.info("Notification for bucket {} created.", bucketName);
              } else {
                  logger.info("Notification for bucket {} already exists.", bucketName);
              }
            } catch (Exception e) {
                logger.error("Error configuring notification for bucket: " + bucketName, e);
            }
        });
    }
}