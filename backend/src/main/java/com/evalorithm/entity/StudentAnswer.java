package com.evalorithm.entity;

import com.evalorithm.enums.AnswerStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "student_answers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentAnswer extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "attempt_id", nullable = false)
    private ExamAttempt attempt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exam_question_id", nullable = false)
    private ExamQuestion examQuestion;

    private String selectedOptionLabel;

    @Column(columnDefinition = "TEXT")
    private String selectedOptionIds;

    @Column(columnDefinition = "TEXT")
    private String textAnswer;

    private Boolean isCorrect;

    @Builder.Default
    private Double marksAwarded = 0.0;

    private Integer timeTakenSeconds;

    private LocalDateTime answeredAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private AnswerStatus answerStatus = AnswerStatus.NOT_ANSWERED;
}
