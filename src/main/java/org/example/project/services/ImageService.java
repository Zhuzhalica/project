package org.example.project.services;

import lombok.RequiredArgsConstructor;
import org.example.project.dataModels.mappers.ImageMapper;
import org.example.project.dataModels.models.MetaImageInfo;
import org.example.project.dataModels.validators.ImageValidator;
import org.example.project.exceptions.custom.EntityNotFoundException;
import org.example.project.repositories.ImageRepository;
import org.example.project.repositories.MetaImageInfoRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ImageService {
    private final ImageRepository imageRepository;
    private final MetaImageInfoRepository metaRepository;
    private final ImageValidator validator;
    private final ImageMapper mapper = ImageMapper.INSTANCE;

    public UUID loadImage(MultipartFile file, long userId) throws Exception {
        validator.validateImage(file);

        var id = imageRepository.loadImage(file);

        var metaInfo = mapper.toImageMetaInfo(file);
        metaInfo.setUserId(userId);
        metaInfo.setId(id);
        metaRepository.save(metaInfo);

        return id;
    }

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

    public List<MetaImageInfo> getUserImageMeta(Long userId) {
        return metaRepository.findAllByUserId(userId);
    }
}
