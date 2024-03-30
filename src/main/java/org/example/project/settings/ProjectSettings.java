package org.example.project.settings;

import com.google.common.collect.ImmutableSet;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
public class ProjectSettings {
    private long maxImageSize;
    private Set<String> imageContentTypes;
}
