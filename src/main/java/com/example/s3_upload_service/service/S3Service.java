package com.example.s3_upload_service.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.Duration;
import java.util.Date;
import java.util.Objects;

@Service
public class S3Service {

    @Autowired
    private S3Client s3Client;

    @Value("${aws.s3.bucket}")
    private String bucketName;

    public String uploadFile(MultipartFile file) {
        File convertedFile = convertMultiPartToFile(file);
        String fileName = generateFileName(file);

        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(fileName)
                .acl("private")
                .build();

        s3Client.putObject(putObjectRequest, RequestBody.fromFile(convertedFile));
        convertedFile.deleteOnExit();

        return s3Client.utilities().getUrl(b -> b.bucket(bucketName).key(fileName)).toString();
    }
    public String createPreSignedGetUrl(String keyName) {
        try (S3Presigner preSigner = S3Presigner.create()) {

            GetObjectRequest objectRequest = GetObjectRequest.builder()
                    .bucket(bucketName)
                    .key(keyName)
                    .build();

            GetObjectPresignRequest preSignRequest = GetObjectPresignRequest.builder()
                    .signatureDuration(Duration.ofMinutes(10))
                    .getObjectRequest(objectRequest)
                    .build();

            PresignedGetObjectRequest preSignedRequest = preSigner.presignGetObject(preSignRequest);

            return preSignedRequest.url().toExternalForm();
        }
    }

    private File convertMultiPartToFile(MultipartFile file) {
        File convertedFile = new File(Objects.requireNonNull(file.getOriginalFilename()));
        try (FileOutputStream fos = new FileOutputStream(convertedFile)) {
            fos.write(file.getBytes());
        } catch (IOException e) {
            throw new RuntimeException("Error converting file", e);
        }
        return convertedFile;
    }

    private String generateFileName(MultipartFile file) {
        return new Date().getTime() + "-" + Objects.requireNonNull(file.getOriginalFilename()).replace(" ", "_");
    }
}
