package com.live.main.common.database.repository;

import com.live.main.common.database.entity.AlertEventEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface AlertRepository extends JpaRepository<AlertEventEntity,Long> {

    @Transactional
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("delete from AlertEventEntity a where a.targetUser = :targetUser")
    void deleteByTargetUser(String targetUser);

    @Transactional
    @Modifying
    @Query("update AlertEventEntity a set a.read = true where a.id = ?1")
    int updateReadById(Long id);

    @Transactional
    @Modifying
    @Query("update AlertEventEntity a set a.read = true where a.targetUser = ?1")
    int updateReadByTargetUser(String targetUser);

    Page<AlertEventEntity> findByTargetUser(String targetUser, Pageable pageable);
}