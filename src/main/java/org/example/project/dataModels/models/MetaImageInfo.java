package org.example.project.dataModels.models;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "image_meta")
@Entity
@ToString
@EqualsAndHashCode
public class MetaImageInfo {
    @Id
    private UUID id;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "name")
    private String name;

    @Column(name = "size")
    private Long size;
}
