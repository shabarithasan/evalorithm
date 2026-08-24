package com.evalorithm.controller;

import com.evalorithm.dto.response.ApiResponse;
import com.evalorithm.dto.response.LeaderboardResponse;
import com.evalorithm.service.LeaderboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/leaderboards")
@RequiredArgsConstructor
@Tag(name = "Leaderboards", description = "Leaderboard and ranking endpoints")
public class LeaderboardController {

    private final LeaderboardService leaderboardService;

    @GetMapping("/departments")
    @Operation(summary = "Get department leaderboard", description = "Get departments ranked by average score")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getDepartmentLeaderboard() {
        List<Map<String, Object>> response = leaderboardService.getDepartmentLeaderboard();
        return ResponseEntity.ok(ApiResponse.success("Department leaderboard retrieved", response));
    }

    @GetMapping("/students")
    @Operation(summary = "Get student leaderboard", description = "Get students ranked by overall score")
    public ResponseEntity<ApiResponse<List<LeaderboardResponse>>> getStudentLeaderboard(
            @RequestParam(defaultValue = "20") int limit) {
        List<LeaderboardResponse> response = leaderboardService.getStudentLeaderboard(limit);
        return ResponseEntity.ok(ApiResponse.success("Student leaderboard retrieved", response));
    }

    @GetMapping("/faculty")
    @Operation(summary = "Get faculty leaderboard", description = "Get faculty ranked by student pass rate")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getFacultyLeaderboard() {
        List<Map<String, Object>> response = leaderboardService.getFacultyLeaderboard();
        return ResponseEntity.ok(ApiResponse.success("Faculty leaderboard retrieved", response));
    }

    @GetMapping("/subjects")
    @Operation(summary = "Get subject leaderboard", description = "Get subjects ranked by average performance")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getSubjectLeaderboard() {
        List<Map<String, Object>> response = leaderboardService.getSubjectLeaderboard();
        return ResponseEntity.ok(ApiResponse.success("Subject leaderboard retrieved", response));
    }
}
