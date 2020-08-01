package com.rjdiscbots.tftbot.discord.message;

import com.rjdiscbots.tftbot.db.items.ItemEntity;
import com.rjdiscbots.tftbot.db.items.ItemsRepository;
import com.rjdiscbots.tftbot.exceptions.message.EntityDoesNotExistException;
import com.rjdiscbots.tftbot.exceptions.message.InvalidMessageException;
import com.rjdiscbots.tftbot.exceptions.message.NoArgumentProvidedException;
import com.rjdiscbots.tftbot.utility.DiscordMessageHelper;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import net.dv8tion.jda.api.EmbedBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

@Component
public class BuildMessageEventHandler {

    private ItemsRepository itemsRepository;

    @Autowired
    public BuildMessageEventHandler(ItemsRepository itemsRepository) {
        this.itemsRepository = itemsRepository;
    }

    public void handleEmbedBuildMessage(@NonNull String rawBuildMessage,
        @NonNull EmbedBuilder embedBuilder, @NonNull Map<String, String> filePathMap)
        throws InvalidMessageException {
        if (!rawBuildMessage.startsWith("!build ")) {
            throw new IllegalArgumentException(
                "Message does begin with !build: " + rawBuildMessage);
        }

        rawBuildMessage = rawBuildMessage.replaceFirst("!build ", "");
        rawBuildMessage = rawBuildMessage.trim();

        boolean addDesc = false;
        if (rawBuildMessage.startsWith("--desc")) {
            rawBuildMessage = rawBuildMessage.replaceFirst("--desc", "").trim();
            addDesc = true;
        }

        fetchItemBuilds(rawBuildMessage, addDesc, embedBuilder);

        String picUrl = "pengu.png";
        filePathMap.put(picUrl, "patch/" + picUrl);
        embedBuilder.setThumbnail("attachment://" + picUrl);
    }

    private void fetchItemBuilds(String components, boolean addDesc, EmbedBuilder embedBuilder)
        throws EntityDoesNotExistException, NoArgumentProvidedException {
        List<String> componentsList = Arrays.stream(components.split("[,]")).map(String::trim)
            .collect(Collectors.toList());

        if (componentsList.size() == 0) {
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
            if (addDesc) {
                desc.append(itemEntity.getDescription());
            }
            String componentOneName = DiscordMessageHelper
                .formatName(itemEntity.getComponentOneName());
            String componentTwoName = DiscordMessageHelper
                .formatName(itemEntity.getComponentTwoName());

            desc.append(" (").append(componentOneName).append(", ")
                .append(componentTwoName).append(")");
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
