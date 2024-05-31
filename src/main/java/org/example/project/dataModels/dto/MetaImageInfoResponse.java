package org.example.project.dataModels.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
@ToString
public class MetaImageInfoResponse implements Serializable {
    private String filename;
    private int size;
    private String imageId;
}
