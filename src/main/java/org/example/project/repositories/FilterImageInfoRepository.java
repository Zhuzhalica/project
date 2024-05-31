package org.example.project.repositories;

import java.util.UUID;
import org.example.project.data.models.models.FilterImageInfo;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository with info about filter image status.
 */
public interface FilterImageInfoRepository extends JpaRepository<FilterImageInfo, UUID> {

}
