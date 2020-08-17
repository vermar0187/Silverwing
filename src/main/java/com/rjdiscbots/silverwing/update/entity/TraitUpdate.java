package com.rjdiscbots.silverwing.update.entity;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.rjdiscbots.silverwing.db.synergies.SetModel;
import com.rjdiscbots.silverwing.db.synergies.SynergyEntity;
import com.rjdiscbots.silverwing.db.synergies.SynergyRepository;
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
public class TraitUpdate implements UpdateEntity {

    private SynergyRepository synergyRepository;

    private final Logger logger = LoggerFactory.getLogger(TraitUpdate.class);

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
    public void save(JsonNode traits) throws JsonFieldDoesNotExistException {
        if (traits.isMissingNode()) {
            throw new JsonFieldDoesNotExistException("Trait set does not exist");
        }

        Iterator<JsonNode> traitIterator = traits.elements();
        List<SynergyEntity> synergyEntities = new ArrayList<>();

        while (traitIterator.hasNext()) {
            JsonNode traitNode = traitIterator.next();

            SynergyEntity synergyEntity;
            try {
                synergyEntity = createSynergyEntity(traitNode);
                synergyEntities.add(synergyEntity);
            } catch (Exception e) {
                logger.info(e.getMessage(), e.fillInStackTrace());
            }
        }

        try {
            synergyRepository.saveAll(synergyEntities);
        } catch (Exception e) {
            logger.warn(e.getMessage(), e.fillInStackTrace());
        }
    }

    private SynergyEntity createSynergyEntity(@NonNull JsonNode synergyNode) {
        SynergyEntity synergyEntity = new SynergyEntity();
        synergyEntity.setKey(JsonParserHelper.stringFieldParse("key", synergyNode));
        synergyEntity.setName(JsonParserHelper.stringFieldParse("name", synergyNode).toLowerCase());
        synergyEntity.setDescription(JsonParserHelper.stringFieldParse("description", synergyNode));
        synergyEntity.setInnate(JsonParserHelper.stringFieldParse("innate", synergyNode));
        synergyEntity.setType(JsonParserHelper.stringFieldParse("type", synergyNode));

        List<SetModel> setModels = new ArrayList<>();
        JsonNode setNodes = synergyNode.get("sets");

        if (setNodes != null && setNodes.isArray()) {
            ArrayNode traitNodeArray = (ArrayNode) setNodes;
            for (JsonNode traitNode : traitNodeArray) {
                SetModel setModel = new SetModel();
                setModel.setMax(JsonParserHelper.integerFieldParse("max", traitNode));
                setModel.setMin(JsonParserHelper.integerFieldParse("min", traitNode));
                setModel.setStyle(JsonParserHelper.stringFieldParse("style", traitNode));
                setModels.add(setModel);
            }
        }
        synergyEntity.setSetModel(setModels);

        return synergyEntity;
    }
}
