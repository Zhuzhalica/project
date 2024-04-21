package org.example.project.data.models.dto;

import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.example.project.data.models.enums.Filter;

/**
 * Message for kafka on work with filter image.
 */
@Getter
@Setter
@AllArgsConstructor
@ToString
public class FilterMessage {

  private UUID imageId;
  private UUID requestId;
  private List<Filter> filters;

}
