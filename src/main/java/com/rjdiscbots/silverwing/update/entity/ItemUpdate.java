package com.rjdiscbots.silverwing.update.entity;

import com.fasterxml.jackson.databind.JsonNode;
import com.rjdiscbots.silverwing.db.items.ItemEntity;
import com.rjdiscbots.silverwing.db.items.ItemsRepository;
import com.rjdiscbots.silverwing.exceptions.parser.JsonFieldDoesNotExistException;
import com.rjdiscbots.silverwing.exceptions.parser.PatchProcessingException;
import com.rjdiscbots.silverwing.update.UpdateEntity;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;

public class ItemUpdate implements UpdateEntity {

    private ItemsRepository itemsRepository;

    private final Logger logger = LoggerFactory.getLogger(GalaxyUpdate.class);

    @Autowired
    public ItemUpdate(ItemsRepository itemsRepository) {
        this.itemsRepository = itemsRepository;
    }

    @Override
    public void patch() throws PatchProcessingException, IOException {
        File itemPatch = new File("patch/items.json");

        JsonNode items = objectMapper.readTree(itemPatch);

        save(items);
    }

    // TODO: Need to update items based off patch in Discord Config
    @Override
    public JsonNode update(JsonNode oldEntity, JsonNode newEntity) throws PatchProcessingException {
        return null;
    }

    @Override
    public void save(JsonNode items) throws JsonFieldDoesNotExistException {
        if (items.isMissingNode()) {
            throw new JsonFieldDoesNotExistException("Item set does not exist");
        }

        Iterator<JsonNode> itemsIterator = items.elements();

        while (itemsIterator.hasNext()) {
            JsonNode itemNode = itemsIterator.next();

            ItemEntity itemEntity = null;

            try {
                itemEntity = createGalaxyEntity(itemNode);
            } catch (Exception e) {
                logger.info(e.getMessage(), e.fillInStackTrace());
                continue;
            }

            try {
                itemsRepository.save(itemEntity);
            } catch (Exception e) {
                logger.warn(e.getMessage(), e.fillInStackTrace());
            }
        }
    }

    private ItemEntity createGalaxyEntity(@NonNull JsonNode itemNode) {
        ItemEntity itemEntity = new ItemEntity();
        itemEntity.setId(itemNode.get("id").asInt());
        itemEntity.setName(itemNode.get("name").asText());
        itemEntity.setDescription(itemNode.get("description").asText());
        itemEntity.setComponentOne(itemNode.get("component1").asInt());
        itemEntity.setComponentTwo(itemNode.get("component2").asInt());
        itemEntity.setComponentOneName(itemNode.get("component1_name").asText());
        itemEntity.setComponentTwoName(itemNode.get("component2_name").asText());
        return itemEntity;
    }
}
