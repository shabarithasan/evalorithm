package com.evalorithm.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "exam_results",
       uniqueConstraints = @UniqueConstraint(columnNames = {"exam_id", "student_profile_id"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExamResult extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exam_id", nullable = false)
    private Exam exam;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_profile_id", nullable = false)
    private StudentProfile studentProfile;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "attempt_id")
    private ExamAttempt attempt;

    private Double totalMarksObtained;

    private Integer totalMarksPossible;

    private Double percentage;

    private String grade;

    @Column(nullable = false)
    private Boolean isPassed;

    private Integer correctAnswers;

    private Integer wrongAnswers;

    private Integer skippedQuestions;

    private Integer timeTakenMinutes;

    private LocalDateTime evaluatedAt;
}
