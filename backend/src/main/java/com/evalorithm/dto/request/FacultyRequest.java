package com.evalorithm.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FacultyRequest {

    @NotBlank(message = "Faculty ID is required")
    private String facultyId;

    private String firstName;
    private String lastName;
    private String email;
    private String phone;

    @NotNull(message = "Department ID is required")
    private Long departmentId;

    private String designation;

    private List<Long> assignedSubjectIds;
}
