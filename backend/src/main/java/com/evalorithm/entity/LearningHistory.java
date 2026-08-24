package com.evalorithm.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "learning_history")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LearningHistory extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_profile_id", nullable = false)
    private StudentProfile studentProfile;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subject_id", nullable = false)
    private Subject subject;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "topic_id")
    private Topic topic;

    @Column(nullable = false)
    private String activity;

    private Double score;

    private Integer timeSpentMinutes;

    private String difficultyLevel;

    @Builder.Default
    private LocalDateTime recordedAt = LocalDateTime.now();
}
