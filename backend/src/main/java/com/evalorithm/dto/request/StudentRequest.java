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
public class StudentRequest {

    @NotBlank(message = "Register number is required")
    private String registerNumber;

    private String firstName;
    private String lastName;
    private String email;
    private String phone;

    @NotNull(message = "Department ID is required")
    private Long departmentId;

    @NotNull(message = "Semester ID is required")
    private Long semesterId;

    private List<Long> enrolledSubjectIds;
}
