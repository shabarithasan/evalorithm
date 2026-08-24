package com.evalorithm.controller;

import com.evalorithm.dto.request.StudentRequest;
import com.evalorithm.dto.response.ApiResponse;
import com.evalorithm.dto.response.PageResponse;
import com.evalorithm.dto.response.StudentResponse;
import com.evalorithm.service.StudentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/students")
@RequiredArgsConstructor
@Tag(name = "Students", description = "Student management endpoints")
public class StudentController {

    private final StudentService studentService;

    @GetMapping
    @Operation(summary = "Get all students")
    public ResponseEntity<ApiResponse<PageResponse<StudentResponse>>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        PageRequest pageRequest = PageRequest.of(page, size, sort);
        PageResponse<StudentResponse> response = studentService.getAll(pageRequest);
        return ResponseEntity.ok(ApiResponse.success("Students retrieved", response));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get student by ID")
    public ResponseEntity<ApiResponse<StudentResponse>> getById(@PathVariable Long id) {
        StudentResponse response = studentService.getById(id);
        return ResponseEntity.ok(ApiResponse.success("Student retrieved", response));
    }

    @GetMapping("/department/{departmentId}")
    @Operation(summary = "Get students by department")
    public ResponseEntity<ApiResponse<List<StudentResponse>>> getByDepartment(@PathVariable Long departmentId) {
        List<StudentResponse> response = studentService.getByDepartment(departmentId);
        return ResponseEntity.ok(ApiResponse.success("Students retrieved", response));
    }

    @GetMapping("/semester/{semesterId}")
    @Operation(summary = "Get students by semester")
    public ResponseEntity<ApiResponse<List<StudentResponse>>> getBySemester(@PathVariable Long semesterId) {
        List<StudentResponse> response = studentService.getBySemester(semesterId);
        return ResponseEntity.ok(ApiResponse.success("Students retrieved", response));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create student")
    public ResponseEntity<ApiResponse<StudentResponse>> create(@Valid @RequestBody StudentRequest request) {
        StudentResponse response = studentService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Student created", response));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update student")
    public ResponseEntity<ApiResponse<StudentResponse>> update(@PathVariable Long id,
                                                                @Valid @RequestBody StudentRequest request) {
        StudentResponse response = studentService.update(id, request);
        return ResponseEntity.ok(ApiResponse.success("Student updated", response));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete student")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        studentService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Student deleted"));
    }

    @PostMapping("/{id}/enroll-subjects")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Enroll student in subjects")
    public ResponseEntity<ApiResponse<StudentResponse>> enrollSubjects(@PathVariable Long id,
                                                                        @RequestBody List<Long> subjectIds) {
        StudentResponse response = studentService.enrollSubjects(id, subjectIds);
        return ResponseEntity.ok(ApiResponse.success("Subjects enrolled", response));
    }

    @PostMapping("/{id}/unenroll-subjects")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Unenroll student from subjects")
    public ResponseEntity<ApiResponse<StudentResponse>> unenrollSubjects(@PathVariable Long id,
                                                                          @RequestBody List<Long> subjectIds) {
        StudentResponse response = studentService.unenrollSubjects(id, subjectIds);
        return ResponseEntity.ok(ApiResponse.success("Subjects unenrolled", response));
    }
}
