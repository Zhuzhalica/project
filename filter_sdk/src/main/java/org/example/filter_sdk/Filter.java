package org.example.filter_sdk;

/**
 * Filters.
 */
public enum Filter {
  BlackAndWhite,
  ColorInverse,
  Mosaic,
  OldPhoto,
  Posterize,
  Tags;

  public static Filter toFilter(String filter) {
    return Filter.valueOf(filter);
  }
}
