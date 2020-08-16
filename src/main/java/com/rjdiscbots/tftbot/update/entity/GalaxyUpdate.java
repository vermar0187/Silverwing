package com.rjdiscbots.tftbot.update.entity;

import com.fasterxml.jackson.databind.JsonNode;
import com.rjdiscbots.tftbot.db.galaxies.GalaxiesRepository;
import com.rjdiscbots.tftbot.db.galaxies.GalaxyEntity;
import com.rjdiscbots.tftbot.exceptions.parser.JsonFieldDoesNotExistException;
import com.rjdiscbots.tftbot.exceptions.parser.PatchProcessingException;
import com.rjdiscbots.tftbot.update.UpdateEntity;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;

public class GalaxyUpdate implements UpdateEntity {

    private GalaxiesRepository galaxiesRepository;

    private final Logger logger = LoggerFactory.getLogger(GalaxyUpdate.class);

    @Autowired
    public GalaxyUpdate(GalaxiesRepository galaxiesRepository) {
        this.galaxiesRepository = galaxiesRepository;
    }

    @Override
    public void patch() throws PatchProcessingException, IOException {
        File galaxyPatch = new File("patch/galaxies.json");

        JsonNode galaxies = objectMapper.readTree(galaxyPatch);

        save(galaxies);
    }

    // TODO: Need to update galaxies based off patch in Discord Config
    @Override
    public JsonNode update(JsonNode oldEntity, JsonNode newEntity) throws PatchProcessingException {
        return null;
    }

    @Override
    public void save(JsonNode galaxies) throws JsonFieldDoesNotExistException {
        if (galaxies.isMissingNode()) {
            throw new JsonFieldDoesNotExistException("Galaxy set does not exist");
        }

        Iterator<JsonNode> galaxyIterator = galaxies.elements();

        while (galaxyIterator.hasNext()) {
            JsonNode galaxyNode = galaxyIterator.next();

            GalaxyEntity galaxyEntity = null;

            try {
                galaxyEntity = createGalaxyEntity(galaxyNode);
            } catch (Exception e) {
                logger.info(e.getMessage(), e.fillInStackTrace());
                continue;
            }

            try {
                galaxiesRepository.save(galaxyEntity);
            } catch (Exception e) {
                logger.warn(e.getMessage(), e.fillInStackTrace());
            }
        }
    }

    private GalaxyEntity createGalaxyEntity(@NonNull JsonNode galaxyNode) {
        GalaxyEntity galaxyEntity = new GalaxyEntity();
        galaxyEntity.setDescripiton(galaxyNode.get("key").asText());
        galaxyEntity.setKey(galaxyNode.get("name").asText());
        galaxyEntity.setName(galaxyNode.get("description").asText());
        return galaxyEntity;
    }
}
