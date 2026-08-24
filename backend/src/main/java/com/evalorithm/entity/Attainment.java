package com.evalorithm.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "attainments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Attainment extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "co_id", nullable = false)
    private CourseOutcome co;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subject_id", nullable = false)
    private Subject subject;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "semester_id", nullable = false)
    private Semester semester;

    private String academicYear;

    private Double targetAttainment;

    private Double actualAttainment;

    private Double directAttainment;

    private Double indirectAttainment;

    @Column(nullable = false)
    @Builder.Default
    private Boolean isAchieved = false;

    private LocalDateTime calculatedAt;
}
