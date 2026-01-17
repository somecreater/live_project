package com.live.main.common.database.repository;

import com.live.main.common.database.entity.ManagerMessageEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface ManagerMessageRepository extends JpaRepository<ManagerMessageEntity,Long> {

    Page<ManagerMessageEntity> findByTargetIdOrderByCreatedAtDesc(String targetId, Pageable pageable);

    void deleteByTargetId(String targetId);


    @Transactional
    @Modifying
    @Query("update ManagerMessageEntity m set m.read = true where m.targetId = ?1")
    int updateReadByTargetId(String targetId);

    @Transactional
    @Modifying
    @Query("update ManagerMessageEntity m set m.read = true where m.id = ?1")
    int updateReadById(Long id);
}
