package br.com.rodrigodip.invoicing.bucket;

import java.net.URI;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.RequiredArgsConstructor;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

@Configuration
@EnableConfigurationProperties({ S3ClientProperties.class })
@RequiredArgsConstructor
public class S3ClientConfiguration {

    private final S3ClientProperties properties;

    @Bean
    public S3Client buildS3Client() {
        // Define your service local endpoint
        URI s3ClientEndpoint = URI.create(properties.getEndpointUrl());

        // Define credentials
        AwsBasicCredentials credentials = AwsBasicCredentials.create(
                properties.getAccessKey(),
                properties.getSecretKey());

        // Assemble the client
        return S3Client.builder()
                .endpointOverride(s3ClientEndpoint)
                .credentialsProvider(StaticCredentialsProvider.create(credentials))
                .region(Region.US_EAST_1) // Required by SDK, but ignored by RustFS
                .forcePathStyle(true) // Forces the client to use path-style URLs
                .build();
    }

    @Bean
    public S3Presigner s3Presigner() {
        URI s3ClientEndpoint = URI.create(properties.getEndpointUrl());

        AwsBasicCredentials credentials = AwsBasicCredentials.create(
                properties.getAccessKey(),
                properties.getSecretKey());

        return S3Presigner.builder()
                .endpointOverride(s3ClientEndpoint)
                .credentialsProvider(StaticCredentialsProvider.create(credentials))
                .region(Region.US_EAST_1)
                .build();
    }
}
