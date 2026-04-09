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

@Repository
public interface VideoRepository extends JpaRepository<VideoEntity, Long> {

    @Modifying
    @Transactional
    @Query("DELETE FROM VideoEntity v WHERE v.status = :status AND v.createdAt < :time")
    int deleteOldPendingVideos(@Param("status") Status status, @Param("time") LocalDateTime time);
}
