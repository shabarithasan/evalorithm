package com.evalorithm.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "units",
       uniqueConstraints = @UniqueConstraint(columnNames = {"number", "subject_id"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Unit extends BaseEntity {

    @Column(nullable = false)
    private Integer number;

    @Column(nullable = false)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subject_id", nullable = false)
    private Subject subject;

    private String description;
}
