package org.example.project.repositories;

import java.util.List;
import java.util.UUID;
import org.example.project.data.models.models.MetaImageInfo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MetaImageInfoRepository extends JpaRepository<MetaImageInfo, UUID> {

  List<MetaImageInfo> findAllByUserId(Long animalId);
}
