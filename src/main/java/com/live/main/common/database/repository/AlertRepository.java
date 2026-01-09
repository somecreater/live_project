package com.live.main.common.database.repository;

import com.live.main.common.database.entity.AlertEventEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AlertRepository extends JpaRepository<AlertEventEntity,Long> {

    List<AlertEventEntity> findByTargetUser(String targetUser);

    void deleteByTargetUser(String targetUser);
}