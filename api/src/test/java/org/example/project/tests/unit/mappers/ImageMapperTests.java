package org.example.project.tests.unit.mappers;

import java.util.UUID;
import org.example.project.data.models.mappers.ImageMapper;
import org.example.project.data.models.models.MetaImageInfo;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

public class ImageMapperTests {

  public static ImageMapper mapper = ImageMapper.INSTANCE;

  @Test
  public void toMetaImageInfoResponse() {
    var meta = new MetaImageInfo();
    meta.setId(UUID.randomUUID());
    meta.setUserId(1L);
    meta.setName("name");
    meta.setSize(1L);

    var response = mapper.toMetaImageInfoResponse(meta);

    Assertions.assertEquals(meta.getName(), response.getFilename());
    Assertions.assertEquals(meta.getSize(), response.getSize());
    Assertions.assertEquals(meta.getId().toString(), response.getImageId());
  }

  @Test
  public void toImageMetaInfo() {
    var file = new MockMultipartFile("file", "name.png", "image/png", new byte[100]);

    var meta = mapper.toImageMetaInfo(file);

    Assertions.assertEquals(file.getOriginalFilename(), meta.getName());
    Assertions.assertEquals(file.getSize(), meta.getSize());
    Assertions.assertNull(meta.getId());
    Assertions.assertNull(meta.getUserId());
  }
}
