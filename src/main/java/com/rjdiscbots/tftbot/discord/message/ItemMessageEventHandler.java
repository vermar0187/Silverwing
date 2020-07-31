package com.rjdiscbots.tftbot.discord.message;

import com.rjdiscbots.tftbot.db.items.ItemEntity;
import com.rjdiscbots.tftbot.db.items.ItemsRepository;
import com.rjdiscbots.tftbot.exceptions.message.EntityDoesNotExistException;
import com.rjdiscbots.tftbot.utility.DiscordMessageHelper;
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

    public String handleItemMessage(String rawItemMessage) throws EntityDoesNotExistException {
        if (!rawItemMessage.startsWith("!item ")) {
            throw new IllegalArgumentException(
                "Message does begin with !item: " + rawItemMessage);
        }

        rawItemMessage = rawItemMessage.replaceFirst("!item ", "");
        rawItemMessage = rawItemMessage.trim();

        return fetchItemDescription(rawItemMessage);
    }

    private String fetchItemDescription(String item) throws EntityDoesNotExistException {
        List<ItemEntity> itemEntities = itemsRepository.findByName(item);

        if (itemEntities == null || itemEntities.isEmpty()) {
            throw new EntityDoesNotExistException("Invalid item provided!");
        }

        StringBuilder returnMessage = new StringBuilder();

        ItemEntity itemEntity = itemEntities.get(0);
        String formattedItemName = DiscordMessageHelper.formatName(itemEntity.getName());

        returnMessage.append("__").append(formattedItemName).append("__").append("\n");
        returnMessage.append(itemEntity.getDescription());

        if (itemEntity.getComponentOne() != null && itemEntity.getComponentTwo() != null) {
            String componentOneName = DiscordMessageHelper
                .formatName(itemEntity.getComponentOneName());
            String componentTwoName = DiscordMessageHelper
                .formatName(itemEntity.getComponentTwoName());

            returnMessage.append("\n").append("#1: ").append(componentOneName);
            returnMessage.append("\n").append("#2: ").append(componentTwoName);
        }

        return returnMessage.toString();
    }
}