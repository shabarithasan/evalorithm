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
public class FacultyResponse {

    private Long id;
    private String facultyId;
    private Long userId;
    private String email;
    private String firstName;
    private String lastName;
    private String phone;
    private Long departmentId;
    private String departmentName;
    private String designation;
    private List<SubjectResponse> assignedSubjects;
}
