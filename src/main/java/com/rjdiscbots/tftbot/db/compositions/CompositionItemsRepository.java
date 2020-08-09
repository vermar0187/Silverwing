package com.rjdiscbots.tftbot.db.compositions;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CompositionItemsRepository extends JpaRepository<CompositionItemsEntity, Long> {

    List<CompositionItemsEntity> findByCompName(String compName);
}
