package br.com.rodrigodip.invoicing.bucket;

import org.springframework.http.MediaType;

public record BucketMediaRepresentation(
                String name,
                String path,
                MediaType mediaType,
                Long size) {

}
