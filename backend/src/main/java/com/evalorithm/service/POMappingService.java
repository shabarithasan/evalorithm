package com.evalorithm.service;

import com.evalorithm.dto.request.POMappingRequest;
import com.evalorithm.dto.response.COMappingResponse;

import java.util.List;

public interface POMappingService {

    void mapCOToPO(POMappingRequest request);

    void removeMapping(Long mappingId);

    List<COMappingResponse> getPOsByDepartment(Long departmentId);
}
