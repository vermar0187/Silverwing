package com.rjdiscbots.tftbot.db.synergies;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SynergyRepository extends JpaRepository<SynergyEntity, Long> {

    List<SynergyEntity> findByName(String name);
}