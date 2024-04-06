package org.example.project.settings;

import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * Common project settings.
 */
@Getter
@Setter
@AllArgsConstructor
public class ProjectSettings {

  private long maxImageSize;
  private Set<String> imageContentTypes;
}
