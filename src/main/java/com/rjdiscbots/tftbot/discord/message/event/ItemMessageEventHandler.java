package com.rjdiscbots.tftbot.discord.message.event;

import com.rjdiscbots.tftbot.db.items.ItemEntity;
import com.rjdiscbots.tftbot.db.items.ItemsRepository;
import com.rjdiscbots.tftbot.exceptions.message.EntityDoesNotExistException;
import com.rjdiscbots.tftbot.exceptions.message.InvalidMessageException;
import com.rjdiscbots.tftbot.exceptions.message.NoArgumentProvidedException;
import com.rjdiscbots.tftbot.utility.DiscordMessageHelper;
import java.util.Map;
import net.dv8tion.jda.api.EmbedBuilder;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

@Component
public class ItemMessageEventHandler implements MessageEvent {

    private ItemsRepository itemsRepository;

    @Autowired
    public ItemMessageEventHandler(ItemsRepository itemsRepository) {
        this.itemsRepository = itemsRepository;
    }

    @Override
    public void handleEmbedMessage(@NonNull String rawItemMessage,
        @NonNull EmbedBuilder embedBuilder, @NonNull Map<String, String> filePathMap)
        throws InvalidMessageException {
        if (!rawItemMessage.startsWith("!item")) {
            throw new IllegalArgumentException(
                "Message does not begin with !item: " + rawItemMessage);
        }

        String itemName = rawItemMessage.replaceFirst("!item", "");
        itemName = itemName.trim();

        fetchItemDescription(itemName, embedBuilder, filePathMap);
    }

    private void fetchItemDescription(String item, EmbedBuilder embedBuilder,
        Map<String, String> filePathMap) throws EntityDoesNotExistException,
        NoArgumentProvidedException {
        if (item == null || StringUtils.isAllBlank(item)) {
            throw new NoArgumentProvidedException("No item provided!");
        }

        ItemEntity itemEntity = itemsRepository.findOneByName(item);

        if (itemEntity == null) {
            throw new EntityDoesNotExistException("Invalid item provided!");
        }

        String formattedItemName = DiscordMessageHelper.formatName(itemEntity.getName());

        int itemId = itemEntity.getId();
        String picId = itemId > 9 ? "" + itemId : "0" + itemId;
        String picUrl = picId + ".png";

        filePathMap.put(picUrl, "patch/items/" + picUrl);

        embedBuilder.setTitle(formattedItemName);
        embedBuilder.setDescription(itemEntity.getDescription());
        embedBuilder.setThumbnail("attachment://" + picUrl);

        if (itemEntity.getComponentOne() != null && itemEntity.getComponentTwo() != null) {
            String componentOneName = itemEntity.getComponentOneName().toLowerCase();
            String componentTwoName = itemEntity.getComponentTwoName().toLowerCase();

            ItemEntity componentOneEntity = itemsRepository.findOneByName(componentOneName);
            ItemEntity componentTwoEntity = itemsRepository.findOneByName(componentTwoName);

            if (componentOneEntity != null && componentTwoEntity != null) {
                String formattedComponentOneName = DiscordMessageHelper
                    .formatName(componentOneName);
                String formattedComponentTwoName = DiscordMessageHelper
                    .formatName(componentTwoName);
                embedBuilder.addField("Components",
                    formattedComponentOneName + ", " + formattedComponentTwoName, false);
            }
        }
    }
}