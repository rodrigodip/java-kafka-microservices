package br.com.rodrigodip.invoicing.bucket;

import java.nio.file.Path;
import java.nio.file.Paths;
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

    public void upload(BucketMediaRepresentation file) {
        try {
            Path path = Paths.get(file.path());

            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(properties.getBucketName())
                    .key(file.name())
                    .contentType(file.mediaType().toString())
                    .build();

            System.out.println("Uploading file to RustFS...");

            s3Client.putObject(putObjectRequest, RequestBody.fromFile(path));

            System.out.println("Successfully uploaded " + file.name() + " to bucket.");
        } catch (S3Exception e) {
            System.err.println("RustFS/S3 error occurred: " + e.awsErrorDetails().errorMessage());
            e.printStackTrace();
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
            System.err.println("Erro ao gerar URL no RustFS: " + e.awsErrorDetails().errorMessage());
            throw e;
        }
    }
}