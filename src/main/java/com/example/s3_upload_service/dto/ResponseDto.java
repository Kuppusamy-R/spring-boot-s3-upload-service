package com.example.s3_upload_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResponseDto {

    private boolean success;

    public int statusCode;

    private String message;

    private Object data;
}
