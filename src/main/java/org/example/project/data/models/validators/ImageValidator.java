package org.example.project.data.models.validators;

import lombok.AllArgsConstructor;
import org.example.project.exceptions.custom.ValidateException;
import org.example.project.settings.ProjectSettings;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
@AllArgsConstructor
public class ImageValidator {

  private final ProjectSettings settings;

  public void validateImage(MultipartFile image) {
    if (image.getSize() > settings.getMaxImageSize()) {
      throw new ValidateException("Размер файла слишком большой");
    }

    if (!settings.getImageContentTypes().contains(image.getContentType())) {
      throw new ValidateException("Недопустимый формат файла");
    }
  }
}
