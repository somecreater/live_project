package com.live.main.channel.database.repository;

import com.live.main.channel.database.entity.SubscriptionEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SubscriptionRepository extends JpaRepository<SubscriptionEntity, Long> {

    Page<SubscriptionEntity> findByChannel_name(String channel_name, Pageable pageable);

    Page<SubscriptionEntity> findByUser_login_id(String user_login_id, Pageable pageable);

    Page<SubscriptionEntity> findByUser_login_idAndChannel_nameLike(String user_login_id, String channel_name, Pageable pageable);

    Page<SubscriptionEntity> findByChannel_nameAndUser_login_idLike(String channel_name, String user_login_id, Pageable pageable);

    long deleteByUser_login_idAndChannel_name(String user_login_id, String channel_name);

    long deleteByUser_login_id(String user_login_id);

    long deleteByChannel_name(String channel_name);

    boolean existsByUser_login_idAndChannel_name(String user_login_id, String channel_name);
}
