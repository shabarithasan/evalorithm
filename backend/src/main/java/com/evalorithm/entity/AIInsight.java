package com.evalorithm.entity;

import com.evalorithm.enums.InsightType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "ai_insights")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AIInsight extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private InsightType insightType;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    private String subjectName;

    @Column(name = "insight_value")
    private Double value;

    @Builder.Default
    private LocalDateTime generatedAt = LocalDateTime.now();

    @Column(nullable = false)
    @Builder.Default
    private Boolean isRead = false;
}
