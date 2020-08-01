package com.rjdiscbots.tftbot.db.champions;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ChampionsRepository extends JpaRepository<ChampionsEntity, Long> {

    @Query(value = "select * from champions WHERE :trait = ANY(traits)", nativeQuery = true)
    List<ChampionsEntity> findByTrait(@Param("trait") String trait);

    ChampionsEntity findOneByName(String championName);
}
