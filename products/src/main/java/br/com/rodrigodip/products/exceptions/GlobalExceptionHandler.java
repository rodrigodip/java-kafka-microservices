package br.com.rodrigodip.products.exceptions;

import java.time.Instant;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import br.com.rodrigodip.products.dto.error.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;

@RestControllerAdvice
public class GlobalExceptionHandler {

        @ExceptionHandler(ProductNotFoundException.class)
        public ResponseEntity<ErrorResponse> handleProductNotFound(
                        ProductNotFoundException exception, HttpServletRequest request) {

                ErrorResponse response = new ErrorResponse(
                                Instant.now(),
                                HttpStatus.NOT_FOUND.value(),
                                HttpStatus.NOT_FOUND.getReasonPhrase(),
                                exception.getMessage(),
                                request.getRequestURI());

                return ResponseEntity
                                .status(HttpStatus.NOT_FOUND)
                                .body(response);
        }
}
