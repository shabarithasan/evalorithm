package com.evalorithm.entity;

import com.evalorithm.enums.LearningPriority;
import com.evalorithm.enums.RecommendationType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "recommendations")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Recommendation extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_profile_id", nullable = false)
    private StudentProfile studentProfile;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RecommendationType recommendationType;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LearningPriority priority;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "related_subject_id")
    private Subject relatedSubject;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "related_topic_id")
    private Topic relatedTopic;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "related_unit_id")
    private Unit relatedUnit;

    @Column(nullable = false)
    @Builder.Default
    private Boolean isRead = false;

    @Column(nullable = false)
    @Builder.Default
    private Boolean isAccepted = false;

    @Builder.Default
    private LocalDateTime generatedAt = LocalDateTime.now();
}
