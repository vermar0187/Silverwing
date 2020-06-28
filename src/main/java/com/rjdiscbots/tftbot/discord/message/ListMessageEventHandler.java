package com.rjdiscbots.tftbot.discord.message;

import com.rjdiscbots.tftbot.db.galaxies.GalaxiesRepository;
import com.rjdiscbots.tftbot.db.galaxies.GalaxyEntity;
import com.rjdiscbots.tftbot.db.items.ItemEntity;
import com.rjdiscbots.tftbot.db.items.ItemsRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ListMessageEventHandler {

    private ItemsRepository itemsRepository;

    private GalaxiesRepository galaxiesRepository;

    @Autowired
    public ListMessageEventHandler(ItemsRepository itemsRepository,
        GalaxiesRepository galaxiesRepository) {
        this.itemsRepository = itemsRepository;
        this.galaxiesRepository = galaxiesRepository;
    }

    public String handleListMessage(String rawListMessage) {
        rawListMessage = rawListMessage.replaceFirst("!list ", "");
        rawListMessage = rawListMessage.trim();

        if (rawListMessage.startsWith("galaxies")) {
            return fetchAllGalaxies();
        } else if (rawListMessage.startsWith("components")) {
            return fetchAllComponents();
        } else {
            return "Invalid !list command";
        }
    }

    private String fetchAllGalaxies() {
        List<GalaxyEntity> galaxiesEntities = galaxiesRepository.findAll();
        StringBuilder allGalaxies = new StringBuilder();

        for (int i = 0; i < galaxiesEntities.size(); i++) {
            if (i != 0) {
                allGalaxies.append("\n");
            }
            allGalaxies.append(galaxiesEntities.get(i).getName());
        }

        return allGalaxies.toString();
    }

    private String fetchAllComponents() {
        List<ItemEntity> itemEntities = itemsRepository
            .findByComponentOneIsNullAndComponentTwoIsNull();

        StringBuilder componentsDesc = new StringBuilder();

        if (itemEntities.isEmpty()) {
            return "No such components exists!";
        }

        for (int i = 0; i < itemEntities.size(); i++) {
            if (i != 0) {
                componentsDesc.append("\n");
            }
            ItemEntity itemEntity = itemEntities.get(i);

            componentsDesc.append(itemEntity.getName()).append(": ")
                .append(itemEntity.getDescription());
        }

        return componentsDesc.toString();
    }
}
