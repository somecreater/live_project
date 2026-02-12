package com.live.main.channel.database.repository;

import com.live.main.channel.database.entity.PostEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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

    void deleteByChannelEntity_Id(Long id);

    long deleteByChannelEntity_Name(String name);

    @EntityGraph(attributePaths = {"channel"})
    @Query("SELECT p FROM PostEntity p")
    Page<PostEntity> findAll(Pageable pageable);

    @EntityGraph(attributePaths = {"channel"})
    @Query("SELECT p FROM PostEntity p WHERE" +
           " p.title LIKE CONCAT('%', :title, '%')" )
    Page<PostEntity> findByTitleContains(@Param("title") String title, Pageable pageable);

    @EntityGraph(attributePaths = {"channel"})
    @Query("SELECT p FROM PostEntity p WHERE" +
           " p.content LIKE CONCAT('%', :content, '%')" )
    Page<PostEntity> findByContentContains(@Param("content") String content, Pageable pageable);

    @EntityGraph(attributePaths = {"channel"})
    @Query("SELECT p FROM PostEntity p WHERE" +
           " p.channelEntity.name LIKE CONCAT('%', :name, '%')" )
    Page<PostEntity> findByChannelEntity_NameContains(@Param("name") String name, Pageable pageable);
}
