package com.rjdiscbots.tftbot.db.galaxies;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GalaxiesRepository extends JpaRepository<GalaxiesEntity, Long> {
    List<GalaxiesEntity> findByName(String name);
}
