package br.com.rodrigodip.invoicing.controller;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import br.com.rodrigodip.invoicing.bucket.BucketMediaRepresentation;
import br.com.rodrigodip.invoicing.bucket.BucketService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/invoices")
@RequiredArgsConstructor
public class BucketController {

    private final BucketService bucketService;

    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) {

        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("Selecione um arquivo válido para testar.");
        }

        try {

            // 2. Resolve o MediaType a partir da String enviada pelo arquivo
            MediaType resolvedMediaType = MediaType.parseMediaType(file.getContentType() != null
                    ? file.getContentType()
                    : MediaType.APPLICATION_PDF_VALUE);

            // 3. Constrói o seu Record preenchendo todos os atributos estruturados
            BucketMediaRepresentation representation = new BucketMediaRepresentation(
                    file.getOriginalFilename(),
                    "in-memory-stream", // Caminho fictício já que o arquivo está apenas em memória
                    resolvedMediaType,
                    file.getSize());

            // 4. Envia o nome mapeado e os bytes para o serviço salvar no RustFS
            bucketService.upload(representation);

            return ResponseEntity.ok("Teste concluído! Arquivo processado via representação: " + representation.name());

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body("MediaType inválido enviado no arquivo: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("Erro inesperado no fluxo de teste: " + e.getMessage());
        }

    }

    @GetMapping("/download-url")
    public ResponseEntity<String> getDownloadUrl(@RequestParam("fileName") String fileName) {
        String presignedUrl = bucketService.generatePresignedUrl(fileName);

        // Retorna a URL em formato de texto puro
        return ResponseEntity.ok()
                .contentType(MediaType.TEXT_PLAIN)
                .body(presignedUrl);
    }
}
