package com.live.main.channel.database.repository;

import com.live.main.channel.database.entity.ChannelEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChannelRepository extends JpaRepository<ChannelEntity, Long> {
    Page<ChannelEntity> findByName(String name, Pageable pageable);

    Page<ChannelEntity> findByDescription(String description, Pageable pageable);

    Page<ChannelEntity> findByNameLike(String name, Pageable pageable);

    Page<ChannelEntity> findByDescriptionLike(String description, Pageable pageable);
}
