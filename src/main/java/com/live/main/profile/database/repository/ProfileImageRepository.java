package com.live.main.profile.database.repository;

import com.live.main.profile.database.entity.ProfileImageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProfileImageRepository extends JpaRepository<ProfileImageEntity, Long> {

    Optional<ProfileImageEntity> findByImageName(String imageName);
}
