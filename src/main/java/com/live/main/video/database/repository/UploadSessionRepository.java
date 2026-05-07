package com.live.main.video.database.repository;

import com.live.main.video.database.entity.UploadSessionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UploadSessionRepository extends JpaRepository<UploadSessionEntity, Long> {

    UploadSessionEntity findByUploadIdAndVideoId(String uploadId, Long videoId);
}
