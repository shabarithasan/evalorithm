package com.evalorithm.controller;

import com.evalorithm.dto.response.ApiResponse;
import com.evalorithm.dto.response.QuestionMediaResponse;
import com.evalorithm.service.QuestionMediaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/question-media")
@RequiredArgsConstructor
@Tag(name = "Question Media", description = "Question media management endpoints")
public class QuestionMediaController {

    private final QuestionMediaService questionMediaService;

    @PostMapping("/upload/{questionId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'FACULTY')")
    @Operation(summary = "Upload media for question", description = "Upload a media file for a question (Admin/Faculty)")
    public ResponseEntity<ApiResponse<QuestionMediaResponse>> uploadMedia(
            @PathVariable Long questionId,
            @RequestParam("file") MultipartFile file) {
        QuestionMediaResponse response = questionMediaService.uploadMedia(questionId, file);
        return ResponseEntity.ok(ApiResponse.success("Media uploaded", response));
    }

    @GetMapping("/{questionId}")
    @Operation(summary = "Get media for question")
    public ResponseEntity<ApiResponse<List<QuestionMediaResponse>>> getMedia(@PathVariable Long questionId) {
        List<QuestionMediaResponse> response = questionMediaService.getMedia(questionId);
        return ResponseEntity.ok(ApiResponse.success("Media retrieved", response));
    }

    @DeleteMapping("/{mediaId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'FACULTY')")
    @Operation(summary = "Delete media", description = "Delete a media file (Admin/Faculty)")
    public ResponseEntity<ApiResponse<Void>> deleteMedia(@PathVariable Long mediaId) {
        questionMediaService.deleteMedia(mediaId);
        return ResponseEntity.ok(ApiResponse.success("Media deleted"));
    }
}
