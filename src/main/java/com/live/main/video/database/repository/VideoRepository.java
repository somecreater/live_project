package com.live.main.video.database.repository;

import com.live.main.video.database.entity.ProcessingStatus;
import com.live.main.video.database.entity.VideoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;

@Repository
public interface VideoRepository extends JpaRepository<VideoEntity, Long> {

    /*
    미업로드 상태의 동영상 정보 삭제 기능
    엔티티 구조변경으로, 미사용 상태(2026/9/15 기준)
    @Modifying
    @Transactional
    @Query("DELETE FROM VideoEntity v WHERE v.status = :status AND v.createdAt < :time")
    int deleteOldPendingVideos(@Param("status") Status status, @Param("time") LocalDateTime time);
     */

    @Modifying(
            flushAutomatically = true,
            clearAutomatically = true
    )
    @Query("""
    UPDATE VideoEntity v
       SET v.processingStatus = :nextStatus
     WHERE v.id = :videoId
       AND v.originalObjectKey = :objectKey
       AND v.processingStatus IN :allowedStatuses
    """)
    int updateProcessingStatusIfAllowed(
            @Param("videoId") Long videoId,
            @Param("objectKey") String objectKey,
            @Param("allowedStatuses")
            Collection<ProcessingStatus> allowedStatuses,
            @Param("nextStatus") ProcessingStatus nextStatus
    );

    @Modifying(
            flushAutomatically = true,
            clearAutomatically = true
    )
    @Query("""
    UPDATE VideoEntity v
       SET v.hlsObjectKey = :masterKey,
           v.processingStatus = :ready
     WHERE v.id = :videoId
       AND v.originalObjectKey = :originalKey
       AND v.processingStatus = :transcoding
    """)
    int completeEncodingIfTranscoding(
            @Param("videoId") Long videoId,
            @Param("originalKey") String originalKey,
            @Param("masterKey") String masterKey,
            @Param("transcoding") ProcessingStatus transcoding,
            @Param("ready") ProcessingStatus ready
    );
}
