package com.live.main.channel.database.repository;

import com.live.main.channel.database.entity.ChannelEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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

    @EntityGraph(attributePaths = {"users"})
    @Query("SELECT c FROM ChannelEntity c")
    Page<ChannelEntity> findAll(Pageable pageable);

    @EntityGraph(attributePaths = {"users"})
    @Query("SELECT c FROM ChannelEntity c WHERE c.name LIKE CONCAT('%', :name, '%')" )
    Page<ChannelEntity> findByNameContains(@Param("name") String name, Pageable pageable);

    @EntityGraph(attributePaths = {"users"})
    @Query("SELECT c FROM ChannelEntity c WHERE c.users.loginId LIKE CONCAT('%', :loginId, '%')" )
    Page<ChannelEntity> findByUsers_LoginIdContains(@Param("loginId") String loginId, Pageable pageable);
}
