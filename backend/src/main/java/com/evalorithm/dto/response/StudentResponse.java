package com.evalorithm.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentResponse {

    private Long id;
    private String registerNumber;
    private Long userId;
    private String email;
    private String firstName;
    private String lastName;
    private String phone;
    private Long departmentId;
    private String departmentName;
    private Long semesterId;
    private Integer semesterNumber;
    private List<SubjectResponse> enrolledSubjects;
}
