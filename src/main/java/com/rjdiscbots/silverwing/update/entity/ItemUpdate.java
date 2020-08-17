package com.rjdiscbots.silverwing.update.entity;

import com.fasterxml.jackson.databind.JsonNode;
import com.rjdiscbots.silverwing.db.items.ItemEntity;
import com.rjdiscbots.silverwing.db.items.ItemsRepository;
import com.rjdiscbots.silverwing.exceptions.parser.JsonFieldDoesNotExistException;
import com.rjdiscbots.silverwing.exceptions.parser.PatchProcessingException;
import com.rjdiscbots.silverwing.update.UpdateEntity;
import com.rjdiscbots.silverwing.utility.JsonParserHelper;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

@Component
public class ItemUpdate implements UpdateEntity {

    private ItemsRepository itemsRepository;

    private final Logger logger = LoggerFactory.getLogger(ItemUpdate.class);

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
        List<ItemEntity> itemEntities = new ArrayList<>();

        while (itemsIterator.hasNext()) {
            JsonNode itemNode = itemsIterator.next();

            ItemEntity itemEntity;
            try {
                itemEntity = createGalaxyEntity(itemNode);
                itemEntities.add(itemEntity);
            } catch (Exception e) {
                logger.info(e.getMessage(), e.fillInStackTrace());
            }
        }

        try {
            itemsRepository.saveAll(itemEntities);
        } catch (Exception e) {
            logger.warn(e.getMessage(), e.fillInStackTrace());
        }
    }

    private ItemEntity createGalaxyEntity(@NonNull JsonNode itemNode) {
        ItemEntity itemEntity = new ItemEntity();
        itemEntity.setId(JsonParserHelper.integerFieldParse("id", itemNode));
        itemEntity.setName(JsonParserHelper.stringFieldParse("name", itemNode).toLowerCase());
        itemEntity.setDescription(JsonParserHelper.stringFieldParse("description", itemNode));
        itemEntity.setComponentOne(JsonParserHelper.integerFieldParse("component1", itemNode));
        itemEntity.setComponentTwo(JsonParserHelper.integerFieldParse("component2", itemNode));

        String componentOneName = JsonParserHelper.stringFieldParse("component1_name", itemNode);
        String componentTwoName = JsonParserHelper.stringFieldParse("component1_name", itemNode);
        itemEntity
            .setComponentOneName(componentOneName != null ? componentOneName.toLowerCase() : null);
        itemEntity
            .setComponentTwoName(componentTwoName != null ? componentTwoName.toLowerCase() : null);
        return itemEntity;
    }
}
