package com.rjdiscbots.tftbot.db.items;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ItemsRepository extends JpaRepository<ItemEntity, Long> {
    List<ItemEntity> findByName(String name);

    List<ItemEntity> findByComponentOneIsNullAndComponentTwoIsNull();
}
