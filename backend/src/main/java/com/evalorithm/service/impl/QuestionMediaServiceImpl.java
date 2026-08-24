package com.evalorithm.service.impl;

import com.evalorithm.dto.response.QuestionMediaResponse;
import com.evalorithm.entity.Question;
import com.evalorithm.entity.QuestionMedia;
import com.evalorithm.enums.MediaType;
import com.evalorithm.exception.BadRequestException;
import com.evalorithm.exception.ResourceNotFoundException;
import com.evalorithm.repository.QuestionMediaRepository;
import com.evalorithm.repository.QuestionRepository;
import com.evalorithm.service.QuestionMediaService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class QuestionMediaServiceImpl implements QuestionMediaService {

    private final QuestionMediaRepository questionMediaRepository;
    private final QuestionRepository questionRepository;

    @Value("${file.upload.dir:./uploads}")
    private String uploadDir;

    @Override
    @Transactional
    public QuestionMediaResponse uploadMedia(Long questionId, MultipartFile file) {
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new ResourceNotFoundException("Question", "id", questionId));

        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
        Path uploadPath = Paths.get(uploadDir, "questions", questionId.toString());

        try {
            Files.createDirectories(uploadPath);
            Path filePath = uploadPath.resolve(fileName);
            file.transferTo(filePath.toFile());

            MediaType mediaType = determineMediaType(file.getContentType());

            QuestionMedia media = QuestionMedia.builder()
                    .question(question)
                    .fileName(file.getOriginalFilename())
                    .fileUrl(filePath.toString())
                    .fileType(mediaType)
                    .fileSize(file.getSize())
                    .uploadedAt(LocalDateTime.now())
                    .build();

            media = questionMediaRepository.save(media);

            return QuestionMediaResponse.builder()
                    .id(media.getId())
                    .fileName(media.getFileName())
                    .fileUrl(media.getFileUrl())
                    .fileType(media.getFileType())
                    .fileSize(media.getFileSize())
                    .uploadedAt(media.getUploadedAt())
                    .build();
        } catch (IOException e) {
            throw new BadRequestException("Failed to upload file: " + e.getMessage());
        }
    }

    @Override
    public List<QuestionMediaResponse> getMedia(Long questionId) {
        List<QuestionMedia> mediaList = questionMediaRepository.findByQuestionId(questionId);
        return mediaList.stream()
                .map(m -> QuestionMediaResponse.builder()
                        .id(m.getId())
                        .fileName(m.getFileName())
                        .fileUrl(m.getFileUrl())
                        .fileType(m.getFileType())
                        .fileSize(m.getFileSize())
                        .uploadedAt(m.getUploadedAt())
                        .build())
                .toList();
    }

    @Override
    @Transactional
    public void deleteMedia(Long mediaId) {
        QuestionMedia media = questionMediaRepository.findById(mediaId)
                .orElseThrow(() -> new ResourceNotFoundException("QuestionMedia", "id", mediaId));

        try {
            Path filePath = Paths.get(media.getFileUrl());
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            // Log but don't fail if file deletion fails
        }

        questionMediaRepository.delete(media);
    }

    private MediaType determineMediaType(String contentType) {
        if (contentType == null) return MediaType.IMAGE;
        return switch (contentType.toLowerCase()) {
            case "image/png", "image/jpeg", "image/gif", "image/webp" -> MediaType.IMAGE;
            case "application/pdf" -> MediaType.PDF;
            case "text/plain", "text/x-python", "text/x-java-source" -> MediaType.CODE_SNIPPET;
            default -> MediaType.DIAGRAM;
        };
    }
}
