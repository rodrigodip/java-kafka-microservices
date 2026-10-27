package br.com.rodrigodip.invoicing.controller;

import java.net.URI;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.rodrigodip.invoicing.bucket.BucketService;
import br.com.rodrigodip.invoicing.service.InvoiceStorageKey;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/invoices")
@RequiredArgsConstructor
public class InvoiceController {

    private final BucketService bucketService;

    @GetMapping("/{orderId}/download")
    public ResponseEntity<Void> download(@PathVariable Long orderId) {
        String invoiceKey = InvoiceStorageKey.forOrder(orderId);

        if (!bucketService.exists(invoiceKey)) {
            return ResponseEntity.notFound().build();
        }

        String presignedUrl = bucketService.generatePresignedUrl(invoiceKey);

        return ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create(presignedUrl))
                .build();
    }
}
