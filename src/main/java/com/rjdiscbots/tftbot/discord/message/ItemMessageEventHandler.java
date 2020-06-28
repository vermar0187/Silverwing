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
            ItemEntity componentOne = itemsRepository.findById(itemEntity.getComponentOne())
                .get(0);
            ItemEntity componentTwo = itemsRepository.findById(itemEntity.getComponentTwo())
                .get(0);

            itemDesc.append("\n").append("#1: ").append(componentOne.getName());
            itemDesc.append("\n").append("#2: ").append(componentTwo.getName());
        }

        return itemDesc.toString();
    }
}
