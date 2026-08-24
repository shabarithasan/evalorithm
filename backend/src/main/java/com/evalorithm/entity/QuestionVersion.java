package com.evalorithm.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "question_versions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuestionVersion extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false)
    private Question question;

    @Column(nullable = false)
    private Integer versionNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "updated_by_id")
    private User updatedBy;

    private String changeDescription;

    @Column(columnDefinition = "TEXT")
    private String snapshot;
}
