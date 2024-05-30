package org.example.project.data.models.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Info about image with filter.
 */
@Getter
@Setter
@NoArgsConstructor
public class GetModifiedImageByRequestIdResponse {

  private String imageId;
  private String status;
}
