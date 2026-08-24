package com.evalorithm.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "mcq_options")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MCQOption extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false)
    private Question question;

    @Column(nullable = false)
    private String optionLabel;

    @Column(columnDefinition = "TEXT")
    private String optionText;

    @Column(nullable = false)
    @Builder.Default
    private Boolean isCorrect = false;

    private String explanation;
}
