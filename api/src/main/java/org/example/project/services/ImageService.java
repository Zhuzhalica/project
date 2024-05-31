package org.example.project.services;

import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.example.project.data.models.mappers.ImageMapper;
import org.example.project.data.models.models.MetaImageInfo;
import org.example.project.data.models.validators.ImageValidator;
import org.example.project.exceptions.custom.EntityNotFoundException;
import org.example.project.repositories.ImageRepository;
import org.example.project.repositories.MetaImageInfoRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
 * Service for work with images.
 */
@Service
@RequiredArgsConstructor
public class ImageService {

  private final ImageRepository imageRepository;
  private final MetaImageInfoRepository metaRepository;
  private final ImageValidator validator;
  private final ImageMapper mapper = ImageMapper.INSTANCE;

  /**
   * Load users image.
   *
   * @param file   image
   * @param userId user id
   * @return image id
   * @throws Exception some image save in repository exception.
   */
  public UUID loadImage(MultipartFile file, long userId) throws Exception {
    validator.validateImage(file);

    var id = imageRepository.loadImage(file);

    var metaInfo = mapper.toImageMetaInfo(file);
    metaInfo.setUserId(userId);
    metaInfo.setId(id);
    metaRepository.save(metaInfo);

    return id;
  }

  /**
   * Download image.
   *
   * @param id     image id
   * @param userId user id
   * @return image as bytes
   * @throws Exception some image get from repository exception.
   */
  public byte[] downloadImage(UUID id, long userId) throws Exception {
    var metaInfo = metaRepository.findById(id).orElse(null);

    if (metaInfo == null) {
      throw new EntityNotFoundException("Фотографии с id = " + id + " не существует");
    }

    if (metaInfo.getUserId() != userId) {
      throw new EntityNotFoundException("Нед доступа к фотографии");
    }

    return imageRepository.downloadImage(id);
  }

  /**
   * Delete image.
   *
   * @param id     image id
   * @param userId user id
   * @throws Exception some image delete from repository exception.
   */
  public void deleteImage(UUID id, long userId) throws Exception {
    var metaInfo = metaRepository.findById(id).orElse(null);

    if (metaInfo == null) {
      throw new EntityNotFoundException("Фотографии с id = " + id + " не существует");
    }

    if (metaInfo.getUserId() != userId) {
      throw new EntityNotFoundException("Нед доступа к фотографии");
    }

    imageRepository.deleteImage(id);
    metaRepository.deleteById(id);
  }

  /**
   * Get user images meta info.
   *
   * @param userId user id
   * @return List of user images meta info
   */
  public List<MetaImageInfo> getUserImageMeta(Long userId) {
    return metaRepository.findAllByUserId(userId);
  }
}
