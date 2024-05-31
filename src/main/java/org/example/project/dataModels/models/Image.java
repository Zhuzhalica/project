package org.example.project.dataModels.models;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "image")
@Entity
public class Image {
    @Id
    private UUID id;

    @Lob
    @Column(name = "image", columnDefinition="BLOB")
    private byte[] image;
}
