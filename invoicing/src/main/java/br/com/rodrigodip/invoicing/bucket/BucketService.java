package br.com.rodrigodip.invoicing.bucket;

import java.time.Duration;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

@Service
@RequiredArgsConstructor
public class BucketService {

    private final S3Client s3Client;
    private final S3Presigner s3Presigner;
    private final S3ClientProperties properties;

    public void upload(BucketMediaRepresentation file, byte[] content) {
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(properties.getBucketName())
                .key(file.name())
                .contentType(file.mediaType().toString())
                .build();

        System.out.println("Uploading file to S3 Bucket...");

        s3Client.putObject(putObjectRequest, RequestBody.fromBytes(content));

        System.out.println("Successfully uploaded " + file.name() + " to S3 bucket.");
    }

    public String generatePresignedUrl(String fileName) {
        try {
            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                    .bucket(properties.getBucketName())
                    .key(fileName)
                    .build();

            GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                    .signatureDuration(Duration.ofMinutes(15))
                    .getObjectRequest(getObjectRequest)
                    .build();

            return s3Presigner.presignGetObject(presignRequest).url().toString();

        } catch (S3Exception e) {
            System.err.println("Error generating presigned Url: " + e.awsErrorDetails().errorMessage());
            throw e;
        }
    }
}