package com.rjdiscbots.silverwing.db.synergies;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SynergyRepository extends JpaRepository<SynergyEntity, Long> {

    SynergyEntity findOneByName(String name);
}