package com.live.main.channel.database.repository;

import com.live.main.channel.database.entity.ChannelEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ChannelRepository extends JpaRepository<ChannelEntity, Long> {
    Page<ChannelEntity> findByNameLike(String name, Pageable pageable);

    Page<ChannelEntity> findByDescriptionLike(String description, Pageable pageable);

    boolean existsByNameOrUsers_LoginId(String name, String loginId);

    Optional<ChannelEntity> findByName(String name);

    void deleteByName(String name);

    Optional<ChannelEntity> findByUsers_LoginId(String loginId);

    Page<ChannelEntity> findByNameContains(String name, Pageable pageable);

    Page<ChannelEntity> findByUsers_LoginIdContains(String loginId, Pageable pageable);
}
