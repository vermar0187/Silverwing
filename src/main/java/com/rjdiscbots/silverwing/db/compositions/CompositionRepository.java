package com.rjdiscbots.silverwing.db.compositions;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CompositionRepository extends JpaRepository<CompositionEntity, Long> {

    CompositionEntity findOneByName(String name);
}
