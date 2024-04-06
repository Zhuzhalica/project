package org.example.project.data.models.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
  @Column(name = "image", columnDefinition = "BLOB")
  private byte[] image;
}
