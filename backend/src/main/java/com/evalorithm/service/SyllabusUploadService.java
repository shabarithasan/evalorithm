package com.evalorithm.service;

import com.evalorithm.dto.request.SyllabusUploadRequest;
import com.evalorithm.dto.response.SyllabusUploadResponse;
import org.springframework.web.multipart.MultipartFile;

public interface SyllabusUploadService {

    SyllabusUploadResponse uploadSyllabus(MultipartFile file, SyllabusUploadRequest request, Long userId);
}
