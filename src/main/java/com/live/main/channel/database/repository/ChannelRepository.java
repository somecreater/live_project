package com.live.main.channel.database.repository;

import com.live.main.channel.database.entity.ChannelEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChannelRepository extends JpaRepository<ChannelEntity, Long> {
}
