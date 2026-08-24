package com.evalorithm.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "student_analytics",
       uniqueConstraints = @UniqueConstraint(columnNames = {"student_profile_id", "subject_id"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentAnalytics extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_profile_id", nullable = false)
    private StudentProfile studentProfile;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subject_id", nullable = false)
    private Subject subject;

    @Column(nullable = false)
    @Builder.Default
    private Integer totalQuestionsAttempted = 0;

    @Column(nullable = false)
    @Builder.Default
    private Integer correctAnswers = 0;

    @Column(nullable = false)
    @Builder.Default
    private Integer wrongAnswers = 0;

    @Column(nullable = false)
    @Builder.Default
    private Integer skippedQuestions = 0;

    @Column(nullable = false)
    @Builder.Default
    private Double averageScore = 0.0;

    @Column(nullable = false)
    @Builder.Default
    private Double accuracy = 0.0;

    @Column(nullable = false)
    @Builder.Default
    private Double completionRate = 0.0;

    @Column(nullable = false)
    @Builder.Default
    private Double averageTimePerQuestion = 0.0;

    private String bestDifficultyLevel;

    @Builder.Default
    private LocalDateTime lastCalculatedAt = LocalDateTime.now();
}
