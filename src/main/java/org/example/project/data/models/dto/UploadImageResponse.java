package org.example.project.data.models.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Info after save image.
 */
@Getter
@Setter
@AllArgsConstructor
@ToString
public class UploadImageResponse {

  private String imageId;
}
