package org.example.project.repositories;

import org.example.project.dataModels.models.MetaImageInfo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface MetaImageInfoRepository extends JpaRepository<MetaImageInfo, UUID> {
    List<MetaImageInfo> findAllByUserId(Long animalId);
}
