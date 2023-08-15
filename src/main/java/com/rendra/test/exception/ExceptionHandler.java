package com.rendra.test.exception;

import com.rendra.test.dto.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.time.LocalDateTime;

@ControllerAdvice
@Slf4j
public class ExceptionHandler {
    @org.springframework.web.bind.annotation.ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ResponseEntity<ErrorResponse> handleDogAlreadyExistException(NotFoundException ex) {
        log.error("======== Terjadi kesalahan saat memproses permintaan", ex);
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setTimestamp(LocalDateTime.now());
        errorResponse.setStatus(HttpStatus.CONFLICT.value());
        errorResponse.setError(HttpStatus.CONFLICT.getReasonPhrase());
        errorResponse.setMessage(ex.getMessage());
        errorResponse.setPath("/dogs");

        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ResponseEntity<ErrorResponse> handleDogAlreadyExistException(Exception ex) {
        log.error("======== Terjadi kesalahan saat memproses permintaan", ex);
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setTimestamp(LocalDateTime.now());
        errorResponse.setStatus(HttpStatus.CONFLICT.value());
        errorResponse.setError(HttpStatus.CONFLICT.getReasonPhrase());
        errorResponse.setMessage(ex.getMessage());
        errorResponse.setPath("/dogs/{id}");

        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
    }

}
