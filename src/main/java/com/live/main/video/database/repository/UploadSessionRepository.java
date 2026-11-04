package com.live.main.video.database.repository;

import com.live.main.video.database.entity.UploadSessionEntity;
import com.live.main.video.database.entity.UploadSessionStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface UploadSessionRepository extends JpaRepository<UploadSessionEntity, Long> {

    Optional<UploadSessionEntity> findByUploadIdAndVideoId(String uploadId, Long videoId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
        select session
        from UploadSessionEntity session
        where session.uploadId = :uploadId
          and session.videoId = :videoId
        """)
    Optional<UploadSessionEntity> findByUploadIdAndVideoIdForUpdate(
            @Param("uploadId") String uploadId,
            @Param("videoId") Long videoId
    );

    List<UploadSessionEntity>
    findTop100ByStatusInAndExpiresAtBeforeOrderByExpiresAtAsc(
            Collection<UploadSessionStatus> statuses,
            LocalDateTime expiresAt
    );
}
