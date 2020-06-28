package com.rjdiscbots.tftbot.discord.message;

import com.rjdiscbots.tftbot.db.items.ItemEntity;
import com.rjdiscbots.tftbot.db.items.ItemsRepository;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class BuildMessageEventHandler {

    private ItemsRepository itemsRepository;

    @Autowired
    public BuildMessageEventHandler(ItemsRepository itemsRepository) {
        this.itemsRepository = itemsRepository;
    }

    public String handleBuildMessage(String rawBuildMessage) {
        rawBuildMessage = rawBuildMessage.replaceFirst("!build ", "");
        rawBuildMessage = rawBuildMessage.trim();

        boolean addDesc = false;
        if (rawBuildMessage.startsWith("--desc")) {
            rawBuildMessage = rawBuildMessage.replaceFirst("--desc", "").trim();
            addDesc = true;
        }

        return fetchItemBuilds(rawBuildMessage, addDesc);
    }

    private String fetchItemBuilds(String components, boolean addDesc) {
        List<String> componentsList = Arrays.stream(components.split("[,]")).map(String::trim)
            .collect(Collectors.toList());

        if (componentsList.size() == 0) {
            return "No components provided!";
        }

        Set<ItemEntity> fullItems = fetchItems(componentsList);

        if (fullItems.size() == 0) {
            return "Invalid components provided!";
        }

        StringBuilder itemsDesc = new StringBuilder();

        for (ItemEntity itemEntity : fullItems) {
            itemsDesc.append("**").append(itemEntity.getName()).append("**");
            if (addDesc) {
                itemsDesc.append(": ").append(itemEntity.getDescription());
            }
            itemsDesc.append(" (").append(itemEntity.getComponentOneName()).append(", ")
                .append(itemEntity.getComponentTwoName()).append(")");
            itemsDesc.append("\n");
        }
        return itemsDesc.toString();
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
