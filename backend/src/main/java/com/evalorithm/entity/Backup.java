package com.evalorithm.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "backups")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Backup extends BaseEntity {

    @Column(nullable = false)
    private String fileName;

    private Long fileSize;

    @Column(nullable = false)
    private String backupType;

    @Column(nullable = false)
    @Builder.Default
    private String status = "COMPLETED";

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_id")
    private User createdBy;

    private String filePath;
}
