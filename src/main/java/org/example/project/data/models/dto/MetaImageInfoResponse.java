package org.example.project.data.models.dto;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Meta image info.
 */
@Getter
@Setter
@AllArgsConstructor
@ToString
public class MetaImageInfoResponse implements Serializable {

  private String filename;
  private int size;
  private String imageId;
}
