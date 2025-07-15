package com.example.rightbackend.global.config;

import com.example.rightbackend.global.properties.FCMProperties;
import com.example.rightbackend.global.response.error.FCMError;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.example.rightbackend.global.exception.RestApiException;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;

@Configuration
public class FCMConfig {

    private final FCMProperties fcmProperties;

    public FCMConfig(FCMProperties fcmProperties) {
        this.fcmProperties = fcmProperties;
    }

    @PostConstruct
    public void init() {
        try {
            InputStream serviceAccount = new ClassPathResource(fcmProperties.getFcmJson()).getInputStream();

            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .build();

            if (FirebaseApp.getApps().isEmpty())
                FirebaseApp.initializeApp(options);

        } catch (IOException e) {
            throw new RestApiException(FCMError.FCM_CONFIGURATION_ERROR);
        }
    }
}