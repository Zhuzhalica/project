package org.example.project.data.models.dto;

import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.example.project.data.models.enums.Filter;

/**
 * Filter for apply on image.
 */
@Getter
@Setter
public class ApplyingFilters {

  private List<Filter> filters;
}
