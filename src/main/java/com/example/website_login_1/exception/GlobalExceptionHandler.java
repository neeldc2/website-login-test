package com.example.website_login_1.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(WebsiteException.class)
    public ResponseEntity<ExceptionHandlerDto> handleWebsiteException(WebsiteException websiteException) {
        log.error("", websiteException);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(org.springframework.http.MediaType.APPLICATION_JSON);

        ExceptionHandlerDto exceptionHandlerDto = ExceptionHandlerDto.builder()
                .message(websiteException.getMessage())
                .build();

        return new ResponseEntity<>(exceptionHandlerDto, headers, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ExceptionHandlerDto> handleRuntimeException(RuntimeException runtimeException) {
        log.error("", runtimeException);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(org.springframework.http.MediaType.APPLICATION_JSON);

        ExceptionHandlerDto exceptionHandlerDto = ExceptionHandlerDto.builder()
                .message("Something went wrong")
                .build();

        return new ResponseEntity<>(exceptionHandlerDto, headers, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
