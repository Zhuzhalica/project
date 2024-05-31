package org.example.filter_sdk;


import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Message for kafka on complete filter image.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FilterFinalMessage {

  private UUID imageId;
  private UUID requestId;
}
