package com.live.main.profile.database.repository;

import com.live.main.profile.database.entity.ProfileImageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProfileImageRepository extends JpaRepository<ProfileImageEntity, Long> {

}
