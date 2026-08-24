package com.evalorithm.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "course_outcomes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CourseOutcome extends BaseEntity {

    @Column(nullable = false)
    private String code;

    @Column(columnDefinition = "TEXT")
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subject_id", nullable = false)
    private Subject subject;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id", nullable = false)
    private Department department;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "semester_id", nullable = false)
    private Semester semester;

    private String bloomsLevel;

    @Column(nullable = false)
    @Builder.Default
    private Boolean isAttainable = true;

    @OneToMany(mappedBy = "co", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<COMapping> coMappings = new ArrayList<>();
}
