package com.live.main.channel.database.repository;

import com.live.main.channel.database.entity.CoverEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CoverRepository extends JpaRepository<CoverEntity, Long> {

    Optional<CoverEntity> findByChannel_Name(String name);
}
