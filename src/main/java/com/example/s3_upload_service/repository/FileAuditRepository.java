package com.example.s3_upload_service.repository;

import com.example.s3_upload_service.entity.FileAudit;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FileAuditRepository extends JpaRepository<FileAudit, Long> {
}

