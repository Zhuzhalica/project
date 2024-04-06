package org.example.project.data.models.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Image meta info entity for repository.
 */
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
