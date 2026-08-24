package com.evalorithm.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "faculty_analytics")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FacultyAnalytics extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "faculty_profile_id", nullable = false)
    private FacultyProfile facultyProfile;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subject_id", nullable = false)
    private Subject subject;

    @Column(nullable = false)
    @Builder.Default
    private Integer totalExamsCreated = 0;

    @Column(nullable = false)
    @Builder.Default
    private Double averageClassScore = 0.0;

    @Column(nullable = false)
    @Builder.Default
    private Integer totalStudents = 0;

    @Column(nullable = false)
    @Builder.Default
    private Double passRate = 0.0;

    @Builder.Default
    private LocalDateTime lastCalculatedAt = LocalDateTime.now();
}
