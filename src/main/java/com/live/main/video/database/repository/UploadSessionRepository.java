package com.live.main.video.database.repository;

import com.live.main.video.database.entity.UploadSessionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UploadSessionRepository extends JpaRepository<UploadSessionEntity, Long> {

    Optional<UploadSessionEntity> findByUploadIdAndVideoId(String uploadId, Long videoId);
}
