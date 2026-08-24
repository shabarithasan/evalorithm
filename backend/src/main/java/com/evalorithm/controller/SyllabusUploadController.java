package com.evalorithm.controller;

import com.evalorithm.dto.request.SyllabusUploadRequest;
import com.evalorithm.dto.response.ApiResponse;
import com.evalorithm.dto.response.SyllabusUploadResponse;
import com.evalorithm.entity.User;
import com.evalorithm.exception.ResourceNotFoundException;
import com.evalorithm.repository.UserRepository;
import com.evalorithm.service.SyllabusUploadService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/syllabus-upload")
@RequiredArgsConstructor
@Tag(name = "Syllabus Upload", description = "Syllabus upload, auto-question generation, and exam creation")
public class SyllabusUploadController {

    private final SyllabusUploadService syllabusUploadService;
    private final UserRepository userRepository;

    @PostMapping("/upload")
    @PreAuthorize("hasAnyRole('ADMIN', 'FACULTY')")
    @Operation(summary = "Upload syllabus and auto-generate", description = "Upload syllabus, auto-generate questions, and create exam (Admin/Faculty)")
    public ResponseEntity<ApiResponse<SyllabusUploadResponse>> uploadSyllabus(
            @RequestParam("file") MultipartFile file,
            @RequestParam Long departmentId,
            @RequestParam Long semesterId,
            @RequestParam Long subjectId) {

        Long userId = getCurrentUserId();
        SyllabusUploadRequest request = new SyllabusUploadRequest(departmentId, semesterId, subjectId);
        SyllabusUploadResponse response = syllabusUploadService.uploadSyllabus(file, request, userId);
        return ResponseEntity.ok(ApiResponse.success("Syllabus processed, questions generated, exam created", response));
    }

    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));
        return user.getId();
    }
}
