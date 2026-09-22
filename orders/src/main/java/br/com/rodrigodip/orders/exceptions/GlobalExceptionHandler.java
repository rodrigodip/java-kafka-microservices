package br.com.rodrigodip.orders.exceptions;

import java.time.Instant;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import br.com.rodrigodip.orders.dto.error.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;

@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ErrorResponse> handleValidation(
			MethodArgumentNotValidException ex,
			HttpServletRequest request) {

		String message = ex.getBindingResult()
				.getFieldErrors()
				.stream()
				.map(error -> error.getField() + ": " + error.getDefaultMessage())
				.collect(Collectors.joining(", "));

		ErrorResponse response = new ErrorResponse(
				Instant.now(),
				HttpStatus.BAD_REQUEST.value(),
				"Validation Error",
				message,
				request.getRequestURI());

		return ResponseEntity
				.badRequest()
				.body(response);
	}

	@ExceptionHandler(OrderNotFoundException.class)
	public ResponseEntity<ErrorResponse> handleOrderNotFound(
			OrderNotFoundException exception, HttpServletRequest request) {

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
