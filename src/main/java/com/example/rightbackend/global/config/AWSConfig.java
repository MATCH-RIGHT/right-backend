package com.example.rightbackend.global.config;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.example.rightbackend.global.properties.S3Properties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.rekognition.RekognitionClient;

@Configuration
public class AWSConfig {

    private final S3Properties s3Properties;

    public AWSConfig(S3Properties s3Properties) {
        this.s3Properties = s3Properties;
    }

    @Bean
    public AmazonS3Client amazonS3Client() {
        BasicAWSCredentials awsCreds = new BasicAWSCredentials(s3Properties.getAccessKey(), s3Properties.getSecretKey());
        return (AmazonS3Client) AmazonS3ClientBuilder.standard()
                .withRegion(s3Properties.getRegion())
                .withCredentials(new AWSStaticCredentialsProvider(awsCreds))
                .build();
    }

    @Bean
    public RekognitionClient rekognitionClient() {
        AwsBasicCredentials awsCreds = AwsBasicCredentials.create(
                s3Properties.getAccessKey(),
                s3Properties.getSecretKey()
        );

        return RekognitionClient.builder()
                .region(Region.of(s3Properties.getRegion()))
                .credentialsProvider(StaticCredentialsProvider.create(awsCreds))
                .build();
    }
}