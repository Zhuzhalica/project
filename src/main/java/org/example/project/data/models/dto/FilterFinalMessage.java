package org.example.project.data.models.dto;


import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Message for kafka on complete filter image.
 */
@Getter
@Setter
@NoArgsConstructor
public class FilterFinalMessage {

  private UUID imageId;
  private UUID requestId;
}
