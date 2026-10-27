package br.com.rodrigodip.invoicing.bucket;

import java.time.Duration;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.http.HttpStatusCode;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

@Slf4j
@Service
@RequiredArgsConstructor
public class BucketService {

    private final S3Client s3Client;
    private final S3Presigner s3Presigner;
    private final S3ClientProperties properties;

    public void upload(String fileName, byte[] content) {
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(properties.getBucketName())
                .key(fileName)
                .contentType(MediaType.APPLICATION_PDF_VALUE)
                .build();

        log.info("Uploading file {} to S3 bucket {}", fileName, properties.getBucketName());

        s3Client.putObject(putObjectRequest, RequestBody.fromBytes(content));

        log.info("Successfully uploaded {} to S3 bucket {}", fileName, properties.getBucketName());
    }

    public boolean exists(String fileName) {
        try {
            s3Client.headObject(HeadObjectRequest.builder()
                    .bucket(properties.getBucketName())
                    .key(fileName)
                    .build());
            return true;
        } catch (S3Exception e) {
            if (e.statusCode() == HttpStatusCode.NOT_FOUND) {
                return false;
            }
            throw e;
        }
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
            log.error("Error generating presigned URL for {}", fileName, e);
            throw e;
        }
    }
}
