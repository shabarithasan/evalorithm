package com.evalorithm.entity;

import com.evalorithm.enums.AIDifficulty;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "adaptive_sessions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdaptiveSession extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_profile_id", nullable = false)
    private StudentProfile studentProfile;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subject_id", nullable = false)
    private Subject subject;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private AIDifficulty currentDifficulty = AIDifficulty.MEDIUM;

    @Column(nullable = false)
    @Builder.Default
    private Integer questionsAnswered = 0;

    @Column(nullable = false)
    @Builder.Default
    private Integer correctAnswers = 0;

    @Column(nullable = false)
    @Builder.Default
    private Integer wrongAnswers = 0;

    @Column(nullable = false)
    @Builder.Default
    private Integer streakCount = 0;

    @Column(nullable = false)
    @Builder.Default
    private Integer maxStreak = 0;

    @Column(nullable = false)
    @Builder.Default
    private Boolean isActive = true;

    @Column(nullable = false)
    @Builder.Default
    private LocalDateTime startTime = LocalDateTime.now();

    private LocalDateTime endTime;

    private Double finalScore;

    @Column(columnDefinition = "TEXT")
    private String difficultyHistory;

    @OneToMany(mappedBy = "session", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<QuestionDifficultyHistory> questionDifficultyHistory = new ArrayList<>();
}
