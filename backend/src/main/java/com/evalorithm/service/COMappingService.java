package com.evalorithm.service;

import com.evalorithm.dto.request.COMappingRequest;
import com.evalorithm.dto.response.COMappingResponse;

import java.util.List;

public interface COMappingService {

    COMappingResponse mapQuestionToCO(COMappingRequest request);

    void removeMapping(Long mappingId);

    List<COMappingResponse> getCOsBySubject(Long subjectId);

    List<COMappingResponse> getMappedQuestions(Long coId);
}
