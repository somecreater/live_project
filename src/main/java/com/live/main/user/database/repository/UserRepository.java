package com.live.main.user.database.repository;

import com.live.main.user.database.entity.UsersEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;


public interface UserRepository extends JpaRepository<UsersEntity, Long> {

   Optional<UsersEntity> findByLoginId(String loginId);

  boolean existsByLoginIdOrPhoneOrNickname(String loginId, String phone, String nickname);

  Optional<UsersEntity> findByEmail(String email);

  boolean existsByEmailOrPhoneOrNickname(String email, String phone, String nickname);

  long deleteByLoginId(String loginId);

  boolean existsByLoginIdOrNickname(String loginId, String nickname);

    @Transactional
    @Modifying
    @Query("update UsersEntity u set u.password = ?1 where u.email = ?2")
    int updatePasswordByEmail(String password, String email);

  Page<UsersEntity> findByLoginIdLike(String loginId, Pageable pageable);

  Page<UsersEntity> findByNicknameLike(String nickname, Pageable pageable);

  Page<UsersEntity> findByEmailLike(String email, Pageable pageable);
}
