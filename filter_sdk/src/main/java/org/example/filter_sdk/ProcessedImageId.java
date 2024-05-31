package org.example.filter_sdk;


import java.io.Serializable;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ProcessedImageId implements Serializable {

  private UUID imageId;
  private UUID requestId;
}

