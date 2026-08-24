package com.evalorithm.entity;

import com.evalorithm.enums.Status;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "semesters",
       uniqueConstraints = @UniqueConstraint(columnNames = {"number", "department_id"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Semester extends BaseEntity {

    @Column(nullable = false)
    private Integer number;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id", nullable = false)
    private Department department;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private Status status = Status.ACTIVE;
}
