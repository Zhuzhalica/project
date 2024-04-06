package org.example.project.controllers;


import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.example.project.data.models.dto.MetaImageInfoResponse;
import org.example.project.data.models.dto.UploadImageResponse;
import org.example.project.data.models.mappers.ImageMapper;
import org.example.project.helpers.UserContextHelper;
import org.example.project.services.ImageService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * Controller for manipulate user`s image.
 */
@RestController
@RequiredArgsConstructor
public class ImageController {

  private final ImageService service;
  private final UserContextHelper userContextHelper;
  private final ImageMapper mapper = ImageMapper.INSTANCE;

  @PreAuthorize("hasRole('USER')")
  @PostMapping("/image")
  public UploadImageResponse uploadImage(MultipartFile file) throws Exception {
    var user = userContextHelper.getUserByRequestContext();

    return new UploadImageResponse(service.loadImage(file, user.getId()).toString());
  }

  @PreAuthorize("hasRole('USER')")
  @GetMapping(value = "/image/{imageId}", produces = {MediaType.IMAGE_PNG_VALUE,
      MediaType.IMAGE_JPEG_VALUE})
  public byte[] downloadImage(@PathVariable String imageId) throws Exception {
    var user = userContextHelper.getUserByRequestContext();
    var id = UUID.fromString(imageId);

    return service.downloadImage(id, user.getId());
  }

  @PreAuthorize("hasRole('USER')")
  @DeleteMapping(value = "/image/{imageId}")
  public ResponseEntity<Object> deleteImage(@PathVariable String imageId) throws Exception {
    var user = userContextHelper.getUserByRequestContext();
    var id = UUID.fromString(imageId);

    service.deleteImage(id, user.getId());

    var body = new HashMap<String, Object>();
    body.put("success", "true");
    return new ResponseEntity<>(body, HttpStatus.OK);
  }

  @PreAuthorize("hasRole('USER')")
  @GetMapping("/images")
  public List<MetaImageInfoResponse> getImages() {
    var user = userContextHelper.getUserByRequestContext();

    return service.getUserImageMeta(user.getId()).stream().map(mapper::toMetaImageInfoResponse)
        .collect(Collectors.toList());
  }
}
