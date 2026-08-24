package com.evalorithm.service;

import com.evalorithm.dto.response.QuestionMediaResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface QuestionMediaService {

    QuestionMediaResponse uploadMedia(Long questionId, MultipartFile file);

    List<QuestionMediaResponse> getMedia(Long questionId);

    void deleteMedia(Long mediaId);
}
