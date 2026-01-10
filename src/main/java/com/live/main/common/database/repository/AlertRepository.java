package com.live.main.common.database.repository;

import com.live.main.common.database.entity.AlertEventEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface AlertRepository extends JpaRepository<AlertEventEntity,Long> {

    List<AlertEventEntity> findByTargetUser(String targetUser);

    @Transactional
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("delete from AlertEventEntity a where a.targetUser = :targetUser")
    void deleteByTargetUser(String targetUser);

    @Transactional
    @Modifying
    @Query("update AlertEventEntity a set a.read = ?1 where a.id = ?2")
    int updateReadById(boolean read, Long id);

    @Transactional
    @Modifying
    @Query("update AlertEventEntity a set a.read = ?1 where a.targetUser = ?2")
    int updateReadByTargetUser(boolean read, String targetUser);
}