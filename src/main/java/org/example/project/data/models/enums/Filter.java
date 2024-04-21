package org.example.project.data.models.enums;

/**
 * Filters.
 */
public enum Filter {
  BlackAndWhite;

  public static Filter toFilter(String filter) {
    return Filter.valueOf(filter);
  }
}
