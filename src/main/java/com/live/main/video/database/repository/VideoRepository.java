package com.live.main.video.database.repository;

import com.live.main.video.database.entity.Status;
import com.live.main.video.database.entity.VideoEntity;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

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

    Optional<VideoEntity> findByChannelEntity_NameAndTitle(String name, String title);
}
