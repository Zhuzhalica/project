package org.example.project.data.models.dto;

import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.example.filter_sdk.Filter;

/**
 * Filter for apply on image.
 */
@Getter
@Setter
public class ApplyingFilters {

  private List<Filter> filters;
}
