package com.live.main.channel.database.repository;

import com.live.main.channel.database.entity.SubscriptionEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SubscriptionRepository extends JpaRepository<SubscriptionEntity, Long> {

    Page<SubscriptionEntity> findByChannelName(String channel_name, Pageable pageable);

    List<SubscriptionEntity> findByChannelName(String channel_name);

    Page<SubscriptionEntity> findByUserLoginId(String user_login_id, Pageable pageable);

    Page<SubscriptionEntity> findByUserLoginIdAndChannelNameContaining(String user_login_id, String channel_name, Pageable pageable);

    Page<SubscriptionEntity> findByChannelNameAndUserLoginIdLike(String channel_name, String user_login_id, Pageable pageable);

    long deleteByUserLoginIdAndChannelName(String user_login_id, String channel_name);

    long deleteByUserLoginId(String user_login_id);

    long deleteByChannelName(String channel_name);

    boolean existsByUserLoginIdAndChannelName(String user_login_id, String channel_name);

    Optional<SubscriptionEntity> findByUserLoginIdAndChannelName(String userLoginId, String channelName);
}
