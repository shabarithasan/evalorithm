package com.evalorithm.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "settings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Setting extends BaseEntity {

    @Column(unique = true, nullable = false)
    private String settingKey;

    private String settingValue;

    private String description;
}
