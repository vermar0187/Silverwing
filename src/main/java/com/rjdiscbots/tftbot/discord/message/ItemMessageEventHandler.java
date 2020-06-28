package com.rjdiscbots.tftbot.discord.message;

import com.rjdiscbots.tftbot.db.items.ItemEntity;
import com.rjdiscbots.tftbot.db.items.ItemsRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ItemMessageEventHandler {

    private ItemsRepository itemsRepository;

    @Autowired
    public ItemMessageEventHandler(ItemsRepository itemsRepository) {
        this.itemsRepository = itemsRepository;
    }

    public String handleItemMessage(String rawItemMessage) {
        rawItemMessage = rawItemMessage.replaceFirst("!item ", "");
        rawItemMessage = rawItemMessage.replaceAll("\"", "").toLowerCase();

        List<ItemEntity> itemEntities = itemsRepository.findByName(rawItemMessage);

        StringBuilder itemDesc = new StringBuilder();
        ItemEntity itemEntity = null;

        if (itemEntities.isEmpty()) {
            return "No such item exists!";
        } else {
            itemEntity = itemEntities.get(0);
            itemDesc.append(itemEntity.getDescription());
        }

        if (itemEntity.getComponentOne() != null && itemEntity.getComponentTwo() != null) {
            itemDesc.append("\n").append("#1: ").append(itemEntity.getComponentOneName());
            itemDesc.append("\n").append("#2: ").append(itemEntity.getComponentTwoName());
        }

        return itemDesc.toString();
    }

    public String handleComponentsMessage(String rawMessage) {
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
