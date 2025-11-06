package com.live.user.database.repository;

import com.live.user.database.entity.UsersEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<UsersEntity, Long> {

}
