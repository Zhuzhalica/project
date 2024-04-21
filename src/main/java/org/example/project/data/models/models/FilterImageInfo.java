package org.example.project.data.models.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.project.data.models.enums.FilterStatus;

/**
 * Entity with filter image info.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "filter_image_info")
@Entity
public class FilterImageInfo {

  @Id
  @Column(name = "request_id")
  private UUID requestId;

  @Column(name = "filter_image_id")
  private UUID filterImageId;

  @Column(name = "original_image_id")
  private UUID originalImageId;

  @Column(name = "status")
  private FilterStatus status;
}
