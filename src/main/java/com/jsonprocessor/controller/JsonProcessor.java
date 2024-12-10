package com.jsonprocessor.controller;

import com.jsonprocessor.dto.RequestPayload;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.stream.Collectors;


@RestController
@RequestMapping("/api/v1/json")
@Slf4j
public class JsonProcessor {
    @PostMapping("/process")
    public ResponseEntity<String> process(@Valid @RequestBody RequestPayload payload) {
        log.info("Trace ID: {} ", MDC.get("traceId"));
        log.info("Received Payload: {}", payload);
        String result = String.format("%s %s", payload.getName(), payload.getSurName());
        return ResponseEntity.ok(result);
    }

    @GetMapping("/status")
    public ResponseEntity<String> getStatus() {
       log.info("TraceId: {}", MDC.get("traceId"));
        return ResponseEntity.ok("Up");
    }

    // Handle MethodArgumentNotValidException if validation fails
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<String> handleValidationException(MethodArgumentNotValidException ex) {
        // Collect validation errors from the BindingResult
        String errorMessage = ex.getBindingResult().getFieldErrors().stream()
                .map(fieldError -> fieldError.getField() + ": " + fieldError.getDefaultMessage())
                .collect(Collectors.joining(", "));

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Validation failed: " + errorMessage);
    }

}
