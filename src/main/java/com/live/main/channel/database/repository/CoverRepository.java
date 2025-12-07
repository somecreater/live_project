package com.live.main.channel.database.repository;

import com.live.main.channel.database.entity.ChannelEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CoverRepository extends JpaRepository<ChannelEntity, Long> {

}
