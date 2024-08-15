package com.example.s3_upload_service.config;

import com.example.s3_upload_service.dto.ResponseDto;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.support.MissingServletRequestPartException;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ResponseDto> handleValidationErrors(MethodArgumentNotValidException ex) {

        List<String> errors = ex.getBindingResult().getFieldErrors()
                .stream().map(FieldError::getDefaultMessage).collect(Collectors.toList());

        ResponseDto responseDto = ResponseDto.builder().statusCode(400).message("Bad Request")
                .data(errors).build();
        return new ResponseEntity<>(responseDto, new HttpHeaders(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MissingServletRequestPartException.class)
    public ResponseEntity<ResponseDto> handleMultipartValidationExceptions(MissingServletRequestPartException ex){
        String errorMessage = ex.getMessage();
        List<String> errors = Collections.singletonList(errorMessage);
        ResponseDto responseDto = ResponseDto.builder().statusCode(400).message("Bad Request")
                .data(errors).build();
        return new ResponseEntity<>(responseDto, new HttpHeaders(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public final ResponseEntity<ResponseDto> handleGeneralExceptions(Exception ex) {
        List<String> errors = Collections.singletonList(ex.getMessage());
        ResponseDto responseDto = ResponseDto.builder().statusCode(500).message("Internal Server Error")
                .data(errors).build();
        return new ResponseEntity<>(responseDto, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(RuntimeException.class)
    public final ResponseEntity<ResponseDto> handleRuntimeExceptions(RuntimeException ex) {
        List<String> errors = Collections.singletonList(ex.getMessage());
        ResponseDto responseDto = ResponseDto.builder().statusCode(500).message("Internal Server Error - Runtime")
                .data(errors).build();
        return new ResponseEntity<>(responseDto, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
