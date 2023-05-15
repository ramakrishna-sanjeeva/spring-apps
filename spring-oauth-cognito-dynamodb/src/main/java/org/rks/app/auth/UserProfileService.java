package org.rks.app.auth;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.auth.credentials.AwsSessionCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.GetItemRequest;
import software.amazon.awssdk.services.dynamodb.model.GetItemResponse;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

@Service
public class UserProfileService {

    private DynamoDbClient dynamoDbClient;

    @PostConstruct
    public void initialize() {
        dynamoDbClient = DynamoDbClient.builder().region(Region.AP_SOUTH_1).credentialsProvider(
                        DefaultCredentialsProvider.create())
                .build();
    }

    @CacheEvict(value = "userprofiles", allEntries = true)
    @Scheduled(fixedRateString = "10000")
    public UserProfile getUserProfile(String email) {
        Map<String, AttributeValue> keysMap = new HashMap<>();
        keysMap.put("email", AttributeValue.builder().s(email).build());

        GetItemResponse getItemResponse = dynamoDbClient.getItem(GetItemRequest.builder().tableName("sh-dt-user").key(keysMap).build());
        String profile = getItemResponse.item().get("profile").s();
        return UserProfile.builder().profile(profile).build();
    }
}
