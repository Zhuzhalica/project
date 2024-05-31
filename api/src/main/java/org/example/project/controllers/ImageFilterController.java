package org.example.project.controllers;

import io.github.bucket4j.Bucket;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.example.filter_sdk.Filter;
import org.example.project.data.models.dto.ApplyImageFiltersResponse;
import org.example.project.data.models.dto.GetModifiedImageByRequestIdResponse;
import org.example.project.data.models.enums.FilterStatus;
import org.example.project.helpers.UserContextHelper;
import org.example.project.services.ImageFilterService;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;

/**
 * Controller for apply filter on image.
 */
@RestController
@RequiredArgsConstructor
public class ImageFilterController {

  private final ImageFilterService imageFilterService;
  private final UserContextHelper userContextHelper;
  private final Bucket bucket;

  /**
   * Apply filter on image.
   *
   * @param imageId image id
   * @return info filter image
   */
  @PreAuthorize("hasRole('USER')")
  @PostMapping("/image/{imageId}/filters/apply")
  public ApplyImageFiltersResponse applyImageFilters(@PathVariable String imageId,
      @RequestParam List<String> filters) {
    var user = userContextHelper.getUserByRequestContext();
    var imageUuid = UUID.fromString(imageId);
    var filtersEnum = filters.stream().map(Filter::toFilter).collect(Collectors.toList());
    if (filtersEnum.contains(Filter.Tags)) {
      var canSendRequest = bucket.tryConsume(1);
      if (!canSendRequest) {
        throw new HttpClientErrorException(HttpStatus.TOO_MANY_REQUESTS, "Too mane requests");
      }
    }

    var requestId = imageFilterService.applyImageFilters(imageUuid, filtersEnum, user.getId());

    return new ApplyImageFiltersResponse(requestId.toString());
  }

  /**
   * Get filtered image.
   *
   * @param imageId   image id
   * @param requestId request filter id
   * @return status filter image
   */
  @PreAuthorize("hasRole('USER')")
  @GetMapping("/image/{imageId}/filters/{requestId}")
  public GetModifiedImageByRequestIdResponse getModifiedImageByRequestId(
      @PathVariable String imageId, @PathVariable String requestId) {
    var user = userContextHelper.getUserByRequestContext();
    var imageUuid = UUID.fromString(imageId);
    var requestUuid = UUID.fromString(requestId);

    var filterImageInfo = imageFilterService.getModifiedImageInfo(imageUuid, requestUuid,
        user.getId());

    var result = new GetModifiedImageByRequestIdResponse();
    result.setStatus(filterImageInfo.getStatus().toString());

    if (filterImageInfo.getStatus() == FilterStatus.WIP) {
      result.setImageId(filterImageInfo.getOriginalImageId().toString());
    } else {
      result.setImageId(filterImageInfo.getFilterImageId().toString());

    }

    return result;
  }
}
