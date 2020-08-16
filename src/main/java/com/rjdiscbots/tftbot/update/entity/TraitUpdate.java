package com.rjdiscbots.tftbot.update.entity;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.rjdiscbots.tftbot.db.items.ItemEntity;
import com.rjdiscbots.tftbot.db.synergies.SetModel;
import com.rjdiscbots.tftbot.db.synergies.SynergyEntity;
import com.rjdiscbots.tftbot.db.synergies.SynergyRepository;
import com.rjdiscbots.tftbot.exceptions.parser.JsonFieldDoesNotExistException;
import com.rjdiscbots.tftbot.exceptions.parser.PatchProcessingException;
import com.rjdiscbots.tftbot.update.UpdateEntity;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;

public class TraitUpdate implements UpdateEntity {

    private SynergyRepository synergyRepository;

    private final Logger logger = LoggerFactory.getLogger(GalaxyUpdate.class);

    @Autowired
    public TraitUpdate(SynergyRepository synergyRepository) {
        this.synergyRepository = synergyRepository;
    }

    @Override
    public void patch() throws PatchProcessingException, IOException {
        File traitPatch = new File("patch/traits.json");

        JsonNode traits = objectMapper.readTree(traitPatch);

        save(traits);
    }

    // TODO: Need to update items based off patch in Discord Config
    @Override
    public JsonNode update(JsonNode oldEntity, JsonNode newEntity) throws PatchProcessingException {
        return null;
    }

    @Override
    public void save(JsonNode items) throws JsonFieldDoesNotExistException {
        if (items.isMissingNode()) {
            throw new JsonFieldDoesNotExistException("Trait set does not exist");
        }

        Iterator<JsonNode> traitIterator = items.elements();

        while (traitIterator.hasNext()) {
            JsonNode itemNode = traitIterator.next();

            SynergyEntity synergyEntity = null;

            try {
                synergyEntity = createSynergyEntity(itemNode);
            } catch (Exception e) {
                logger.info(e.getMessage(), e.fillInStackTrace());
                continue;
            }

            try {
                synergyRepository.save(synergyEntity);
            } catch (Exception e) {
                logger.warn(e.getMessage(), e.fillInStackTrace());
            }
        }
    }

    private SynergyEntity createSynergyEntity(@NonNull JsonNode synergyNode) {
        SynergyEntity synergyEntity = new SynergyEntity();

        synergyEntity.setKey(synergyNode.get("key").asText());
        synergyEntity.setName(synergyNode.get("name").asText());
        synergyEntity.setDescription(synergyNode.get("description").asText());
        synergyEntity.setType(synergyNode.get("type").asText());
        synergyEntity.setInnate(synergyNode.get("innate").asText());

        List<SetModel> setModels = new ArrayList<>();
        JsonNode setNodes = synergyNode.get("sets");

        if (setNodes.isArray()) {
            ArrayNode traitNodeArray = (ArrayNode) setNodes;
            for (JsonNode traitNode : traitNodeArray) {
                SetModel setModel = new SetModel();
                setModel.setMax(traitNode.get("max").asInt());
                setModel.setMin(traitNode.get("min").asInt());
                setModel.setStyle(traitNode.get("style").asText());
                setModels.add(setModel);
            }
        }
        synergyEntity.setSetModel(setModels);

        return synergyEntity;
    }
}
