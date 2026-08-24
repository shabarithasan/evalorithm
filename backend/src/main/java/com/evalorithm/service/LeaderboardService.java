package com.evalorithm.service;

import com.evalorithm.dto.response.LeaderboardResponse;

import java.util.List;
import java.util.Map;

public interface LeaderboardService {

    List<Map<String, Object>> getDepartmentLeaderboard();

    List<LeaderboardResponse> getStudentLeaderboard(int limit);

    List<Map<String, Object>> getFacultyLeaderboard();

    List<Map<String, Object>> getSubjectLeaderboard();
}
