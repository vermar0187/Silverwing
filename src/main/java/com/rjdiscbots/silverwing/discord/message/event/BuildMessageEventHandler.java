package com.rjdiscbots.silverwing.discord.message.event;

import com.rjdiscbots.silverwing.db.items.ItemEntity;
import com.rjdiscbots.silverwing.db.items.ItemsRepository;
import com.rjdiscbots.silverwing.exceptions.message.EntityDoesNotExistException;
import com.rjdiscbots.silverwing.exceptions.message.InvalidMessageException;
import com.rjdiscbots.silverwing.exceptions.message.NoArgumentProvidedException;
import com.rjdiscbots.silverwing.utility.DiscordMessageHelper;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import net.dv8tion.jda.api.EmbedBuilder;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

@Component
public class BuildMessageEventHandler implements MessageEvent {

    private ItemsRepository itemsRepository;

    @Autowired
    public BuildMessageEventHandler(ItemsRepository itemsRepository) {
        this.itemsRepository = itemsRepository;
    }

    @Override
    public void handleEmbedMessage(@NonNull String rawBuildMessage,
        @NonNull EmbedBuilder embedBuilder, @NonNull Map<String, String> filePathMap)
        throws InvalidMessageException {
        if (!rawBuildMessage.startsWith("!build")) {
            throw new IllegalArgumentException(
                "Message does not begin with !build: " + rawBuildMessage);
        }

        String itemComponents = rawBuildMessage.replaceFirst("!build", "");
        itemComponents = itemComponents.trim();

        boolean addDesc = false;
        if (itemComponents.startsWith("--desc")) {
            itemComponents = itemComponents.replaceFirst("--desc", "");
            itemComponents = itemComponents.trim();
            addDesc = true;
        }

        fetchItemBuilds(itemComponents, addDesc, embedBuilder);

        String picUrl = "tft_icon.png";
        filePathMap.put(picUrl, "assets/" + picUrl);
        embedBuilder.setThumbnail("attachment://" + picUrl);
    }

    private void fetchItemBuilds(String components, boolean addDesc, EmbedBuilder embedBuilder)
        throws EntityDoesNotExistException, NoArgumentProvidedException {
        List<String> componentsList = Arrays.stream(components.split("[,]")).map(String::trim)
            .collect(Collectors.toList());

        if (componentsList.size() == 0 || StringUtils.isAllBlank(components)) {
            throw new NoArgumentProvidedException("No components provided!");
        }

        Set<ItemEntity> fullItems = fetchItems(componentsList);

        if (fullItems.size() == 0) {
            throw new EntityDoesNotExistException("Invalid components provided!");
        }

        embedBuilder.setTitle("Build Recipes");
        embedBuilder.setDescription("All possible items made from the given components.");

        StringBuilder desc = new StringBuilder();

        for (ItemEntity itemEntity : fullItems) {
            String formattedItemName = DiscordMessageHelper.formatName(itemEntity.getName());

            String componentOneName = DiscordMessageHelper
                .formatName(itemEntity.getComponentOneName());
            String componentTwoName = DiscordMessageHelper
                .formatName(itemEntity.getComponentTwoName());

            if (addDesc) {
                desc.append(itemEntity.getDescription());
                desc.append(" (").append(componentOneName).append(", ").append(componentTwoName)
                    .append(")");
            } else {
                desc.append(componentOneName).append(", ").append(componentTwoName);
            }

            embedBuilder.addField(formattedItemName, desc.toString(), false);
            desc.setLength(0);
        }
    }

    private Set<ItemEntity> fetchItems(List<String> components) {
        Set<ItemEntity> itemEntitySet = new HashSet<>();

        if (components.size() == 1) {
            String componentName = components.get(0);
            itemEntitySet.addAll(
                itemsRepository
                    .findByComponentOneNameOrComponentTwoName(componentName, componentName));
        } else {
            for (int i = components.size() - 1; i >= 0; i--) {
                String component = components.get(i);
                components.remove(i);

                if (!component.isEmpty()) {
                    itemEntitySet.addAll(itemsRepository
                        .findByComponentOneNameAndComponentTwoNameIsInOrComponentTwoNameAndComponentOneNameIsIn(
                            component, components, component, components));
                }
            }
        }

        return itemEntitySet;
    }
}
