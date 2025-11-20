package com.live.main.user.database.repository;

import com.live.main.user.database.entity.UsersEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface UserRepository extends JpaRepository<UsersEntity, Long> {

   Optional<UsersEntity> findByLoginId(String loginId);

  boolean existsByLoginIdOrPhoneOrNickname(String loginId, String phone, String nickname);

  Optional<UsersEntity> findByEmail(String email);

  boolean existsByEmailOrPhoneOrNickname(String email, String phone, String nickname);

  long deleteByLoginId(String loginId);

  boolean existsByLoginIdOrNickname(String loginId, String nickname);
}
