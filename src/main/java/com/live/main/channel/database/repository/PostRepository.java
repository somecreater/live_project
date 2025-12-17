package com.live.main.channel.database.repository;

import com.live.main.channel.database.entity.PostEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface PostRepository extends JpaRepository<PostEntity, Long> {

    Page<PostEntity> findByChannelEntity_Name(String name, Pageable pageable);

    Page<PostEntity> findByVisibilityTrueAndChannelEntity_Name(
      String name, Pageable pageable
    );

    Page<PostEntity> findByVisibilityTrueAndChannelEntity_NameAndTitleLike(
      String name, String title, Pageable pageable
    );

    Page<PostEntity> findByVisibilityTrueAndChannelEntity_NameAndContentLike(
      String name, String content, Pageable pageable
    );

    Page<PostEntity> findByVisibilityTrueAndChannelEntity_NameAndCategoryLike(
      String name, String category, Pageable pageable
    );

    Page<PostEntity> findByChannelEntity_NameAndTitleLike(
      String name, String title, Pageable pageable
    );

    Page<PostEntity> findByChannelEntity_NameAndContentLike(
      String name, String content, Pageable pageable
    );

    Page<PostEntity> findByChannelEntity_NameAndCategoryLike(
      String name, String category, Pageable pageable
    );
}
