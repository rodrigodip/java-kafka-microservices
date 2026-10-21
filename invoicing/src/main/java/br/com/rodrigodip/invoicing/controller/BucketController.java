package br.com.rodrigodip.invoicing.controller;

import java.util.Optional;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import br.com.rodrigodip.invoicing.bucket.BucketMediaRepresentation;
import br.com.rodrigodip.invoicing.bucket.BucketService;
import lombok.RequiredArgsConstructor;
import software.amazon.awssdk.services.s3.model.S3Exception;

@RestController
@RequestMapping("/invoices")
@RequiredArgsConstructor
public class BucketController {

    private final BucketService bucketService;

    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) {

        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("Select a valid file.");
        }

        try {
            MediaType resolvedMediaType = MediaType.parseMediaType(file.getContentType() != null
                    ? file.getContentType()
                    : MediaType.APPLICATION_PDF_VALUE);

            String fileName = Optional.ofNullable(file.getOriginalFilename())
                    .filter(name -> !name.isBlank())
                    .orElse("invoice-" + UUID.randomUUID() + ".pdf");

            BucketMediaRepresentation representation = new BucketMediaRepresentation(
                    fileName,
                    resolvedMediaType,
                    file.getSize());

            bucketService.upload(representation, file.getBytes());

            return ResponseEntity.ok("Success! File representation was uploaded: " + representation.name());

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body("Invalid mediaType: " + e.getMessage());
        } catch (S3Exception e) {
            String errorMessage = e.awsErrorDetails() != null
                    ? e.awsErrorDetails().errorMessage()
                    : e.getMessage();

            return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                    .body("Error uploading file to S3 bucket: " + errorMessage);
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("Unexpected error occurred: " + e.getMessage());
        }

    }

    @GetMapping("/download-url")
    public ResponseEntity<String> getDownloadUrl(@RequestParam("fileName") String fileName) {
        String presignedUrl = bucketService.generatePresignedUrl(fileName);

        return ResponseEntity.ok()
                .contentType(MediaType.TEXT_PLAIN)
                .body(presignedUrl);
    }
}
