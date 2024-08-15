package com.example.s3_upload_service.controller;

import com.example.s3_upload_service.dto.ResponseDto;
import com.example.s3_upload_service.dto.UrlDto;
import com.example.s3_upload_service.entity.FileAudit;
import com.example.s3_upload_service.repository.FileAuditRepository;
import com.example.s3_upload_service.service.S3Service;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;

@RestController
@RequestMapping("/api/files")
public class FileUploadController {

    @Autowired
    private S3Service s3Service;

    @Autowired
    private FileAuditRepository fileAuditRepository;

    @PostMapping("/upload")
    public ResponseEntity<ResponseDto> uploadFile(@RequestParam("file") @NotNull MultipartFile file) {
        Date currentDate = new Date();
        String url = s3Service.uploadFile(file);

        FileAudit fileAudit = new FileAudit();
        fileAudit.setFileName(file.getOriginalFilename());
        fileAudit.setUrl(url);
        fileAudit.setUploadDate(currentDate);
        fileAuditRepository.save(fileAudit);

        ResponseDto responseDto = ResponseDto.builder()
                .success(true)
                .message("File uploaded successfully!")
                .statusCode(200)
                .data(UrlDto.builder().url(url).createdAt(currentDate).build())
                .build();

        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    @GetMapping("/generate-presigned-url")
    public ResponseEntity<ResponseDto> generatePreSignedUrl(@RequestParam("fileName") @NotNull String fileName) {
        String preSignedUrl = s3Service.createPreSignedGetUrl(fileName);

        ResponseDto responseDto = ResponseDto.builder()
                .success(true)
                .message("Pre Signed URL generated successfully!")
                .statusCode(200)
                .data(UrlDto.builder().url(preSignedUrl).createdAt(new Date()).build()).build();

        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

}
