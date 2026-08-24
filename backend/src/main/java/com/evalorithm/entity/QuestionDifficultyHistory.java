package com.evalorithm.entity;

import com.evalorithm.enums.AIDifficulty;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "question_difficulty_history")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuestionDifficultyHistory extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id", nullable = false)
    private AdaptiveSession session;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false)
    private Question question;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AIDifficulty difficulty;

    @Column(nullable = false)
    private Boolean wasCorrect;

    private Integer timeTakenSeconds;

    @Column(nullable = false)
    @Builder.Default
    private LocalDateTime answeredAt = LocalDateTime.now();
}
