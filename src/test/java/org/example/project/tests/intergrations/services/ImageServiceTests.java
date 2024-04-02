package org.example.project.tests.intergrations.services;

import org.example.project.config.BaseTest;
import org.example.project.dataModels.enums.Role;
import org.example.project.dataModels.models.MetaImageInfo;
import org.example.project.dataModels.models.User;
import org.example.project.exceptions.custom.EntityNotFoundException;
import org.example.project.exceptions.custom.ValidateException;
import org.example.project.repositories.MetaImageInfoRepository;
import org.example.project.repositories.UserRepository;
import org.example.project.services.ImageService;
import org.example.project.repositories.ImageRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.UUID;

public class ImageServiceTests extends BaseTest {
    @Autowired
    private ImageRepository imageRepository;

    @Autowired
    private MetaImageInfoRepository metaRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ImageService imageService;

    private byte[] savedImageBytes;

    private MultipartFile defaultImage;

    private User existUser;

    @AfterEach
    void cleanup() {
        userRepository.deleteAll();
    }

    @BeforeEach
    void setup() {
        savedImageBytes = new byte[100];
        defaultImage = new MockMultipartFile("file", "name.png", "image/png", savedImageBytes);
        userRepository.save(new User(1L, "login", "password", Role.USER));
        existUser = userRepository.findByLogin("login").get();
    }

    @Test
    public void loadImage_ShouldThrowValidateException_WhenValidatorThrow() {
        var invalidImage = new MockMultipartFile("file", "name.png", "wrongType", new byte[100]);

        Assertions.assertThrows(ValidateException.class, () -> imageService.loadImage(invalidImage, 1L));
    }

    @Test
    public void loadImage_ShouldSaveImageAndMetaInfo() throws Exception {
        var userId = existUser.getId();

        var imageId = imageService.loadImage(defaultImage, userId);

        var expectedMetaInfo = new MetaImageInfo(imageId, userId, defaultImage.getOriginalFilename(), defaultImage.getSize());
        var imageBytes = imageRepository.downloadImage(imageId);
        var metaInfo = metaRepository.findById(imageId).get();

        Assertions.assertArrayEquals(savedImageBytes, imageBytes);
        Assertions.assertEquals(metaInfo, expectedMetaInfo);
    }

    @Test
    public void downloadImage_ShouldReturnImage() throws Exception {
        var imageId = imageService.loadImage(defaultImage, existUser.getId());

        var fileBytes = imageService.downloadImage(imageId, existUser.getId());

        Assertions.assertArrayEquals(savedImageBytes, fileBytes);
    }

    @Test
    public void downloadImage_ShouldThrowEntityNotFound_WhenImageMetaNotExist() {
        var imageId = UUID.randomUUID();
        var userId = existUser.getId();

        Assertions.assertThrows(EntityNotFoundException.class, () -> imageService.downloadImage(imageId, userId), "Фотографии с id = " + imageId + " не существует");
    }

    @Test
    public void downloadImage_ShouldThrowEntityNotFound_WhenUserNotAccess() throws Exception {
        var userId = existUser.getId();
        var imageId = imageService.loadImage(defaultImage, userId);

        Assertions.assertThrows(EntityNotFoundException.class, () -> imageService.downloadImage(imageId, userId + 1), "Нед доступа к фотографии");
    }

    @Test
    public void deleteImage_ShouldDeleteImage() throws Exception {
        var imageId = imageService.loadImage(defaultImage, existUser.getId());

        imageService.deleteImage(imageId, existUser.getId());

        Assertions.assertThrows(EntityNotFoundException.class, () -> imageService.downloadImage(imageId, existUser.getId()));
    }

    @Test
    public void deleteImage_ShouldThrowEntityNotFound_WhenImageMetaNotExist() {
        var imageId = UUID.randomUUID();
        var userId = 1L;

        Assertions.assertThrows(EntityNotFoundException.class, () -> imageService.deleteImage(imageId, userId), "Фотографии с id = " + imageId + " не существует");
    }

    @Test
    public void deleteImage_ShouldThrowEntityNotFound_WhenUserNotAccess() throws Exception {
        var userId = existUser.getId();
        var imageId = imageService.loadImage(defaultImage, userId);

        Assertions.assertThrows(EntityNotFoundException.class, () -> imageService.deleteImage(imageId, userId + 1), "Нед доступа к фотографии");
    }

    @Test
    public void getUserImageMeta_ShouldReturnInfoFromRepository() throws Exception {
        var userId = existUser.getId();
        var imageId = imageService.loadImage(defaultImage, userId);
        var expectedMetaInfo = new ArrayList<>();
        expectedMetaInfo.add(new MetaImageInfo(imageId, userId, defaultImage.getOriginalFilename(), defaultImage.getSize()));

        var resultMetaList = imageService.getUserImageMeta(userId);

        Assertions.assertEquals(resultMetaList, expectedMetaInfo);
    }
}
