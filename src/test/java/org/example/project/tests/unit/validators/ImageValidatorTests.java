package org.example.project.tests.unit.validators;

import org.example.project.dataModels.validators.ImageValidator;
import org.example.project.exceptions.custom.ValidateException;
import org.example.project.settings.ProjectSettings;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.mock.web.MockMultipartFile;

import java.util.Arrays;
import java.util.HashSet;

public class ImageValidatorTests {
    private static ImageValidator imageValidator;
    private static ProjectSettings settings;

    @BeforeAll
    static void setup() {
        settings = new ProjectSettings(10000, new HashSet<>(Arrays.asList("image/png", "image/jpeg")));

        imageValidator = new ImageValidator(settings);
    }


    @ParameterizedTest
    @ValueSource(strings = {"image/png", "image/jpeg"})
    public void validateImage_ShouldNotThrow_WithValidData(String contentType) {
        Assertions.assertDoesNotThrow(() -> imageValidator.validateImage(new MockMultipartFile(
                "file",
                "name.png",
                contentType,
                new byte[(int) settings.getMaxImageSize()])));

    }

    @Test
    public void validateImage_ShouldThrowValidateException_WithBigSize() {
        Assertions.assertThrows(ValidateException.class, () -> imageValidator.validateImage(new MockMultipartFile(
                "file",
                "name.png",
                "image/png",
                new byte[(int) settings.getMaxImageSize() + 1])), "Размер файла слишком большой");

    }

    @Test
    public void validateImage_ShouldThrowValidateException_WithWrongContentType() {
        Assertions.assertThrows(ValidateException.class, () -> imageValidator.validateImage(new MockMultipartFile(
                "file",
                "name.png",
                "image/png123",
                new byte[(int) settings.getMaxImageSize()])), "Недопустимый формат файла");

    }
}
