package org.example.filter_three.data.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.filter_sdk.ProcessedImageId;


/**
 * Processed filter image entity for repository.
 */
@Entity
@Table(name = "processed_image")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@IdClass(ProcessedImageId.class)
public class ProcessedImage {

  @Id
  private UUID imageId;
  @Id
  private UUID requestId;
}
