package org.example.project.services;

import com.google.gson.Gson;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.example.project.data.models.dto.FilterMessage;
import org.example.project.data.models.enums.Filter;
import org.example.project.data.models.enums.FilterStatus;
import org.example.project.data.models.models.FilterImageInfo;
import org.example.project.exceptions.custom.EntityNotFoundException;
import org.example.project.kafka.FilterKafkaService;
import org.example.project.repositories.FilterImageInfoRepository;
import org.example.project.repositories.MetaImageInfoRepository;
import org.springframework.stereotype.Service;

/**
 * Service for work with filter image.
 */
@Service
@RequiredArgsConstructor
public class ImageFilterService {

  private final MetaImageInfoRepository metaRepository;
  private final FilterImageInfoRepository filterImageInfoRepository;
  private final FilterKafkaService filterKafkaWorker;
  private final Gson jsonConverter = new Gson();


  /**
   * Start work filter on image.
   *
   * @param imageId image id
   * @param filters filter for apply
   * @param userId  user id
   * @return request id
   */
  public UUID applyImageFilters(UUID imageId, List<Filter> filters, long userId) {
    checkImageAccess(imageId, userId);
    var requestId = UUID.randomUUID();

    filterKafkaWorker.write(jsonConverter.toJson(new FilterMessage(imageId, requestId, filters)));

    filterImageInfoRepository.save(
        new FilterImageInfo(requestId, null, imageId, FilterStatus.WIP));

    return requestId;
  }

  /**
   * Get filtered image info.
   *
   * @param imageId   image id
   * @param requestId request id
   * @param userId    user id
   * @return filter image info
   */
  public FilterImageInfo getModifiedImageInfo(UUID imageId, UUID requestId,
      long userId) {
    checkImageAccess(imageId, userId);

    return filterImageInfoRepository.findById(requestId).orElseThrow();
  }

  private void checkImageAccess(UUID imageId, long userId) {
    var metaInfo = metaRepository.findById(imageId).orElse(null);

    if (metaInfo == null) {
      throw new EntityNotFoundException("Фотографии с id = " + imageId + " не существует");
    }

    if (metaInfo.getUserId() != userId) {
      throw new EntityNotFoundException("Нед доступа к фотографии");
    }
  }
}
