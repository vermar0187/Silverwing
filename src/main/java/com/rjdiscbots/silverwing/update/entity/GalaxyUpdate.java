package com.rjdiscbots.silverwing.update.entity;

import com.fasterxml.jackson.databind.JsonNode;
import com.rjdiscbots.silverwing.db.galaxies.GalaxiesRepository;
import com.rjdiscbots.silverwing.db.galaxies.GalaxyEntity;
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
        List<GalaxyEntity> galaxyEntities = new ArrayList<>();

        while (galaxyIterator.hasNext()) {
            JsonNode galaxyNode = galaxyIterator.next();

            GalaxyEntity galaxyEntity;
            try {
                galaxyEntity = createGalaxyEntity(galaxyNode);
                galaxyEntities.add(galaxyEntity);
            } catch (Exception e) {
                logger.info(e.getMessage(), e.fillInStackTrace());
            }
        }

        try {
            galaxiesRepository.saveAll(galaxyEntities);
        } catch (Exception e) {
            logger.warn(e.getMessage(), e.fillInStackTrace());
        }
    }

    private GalaxyEntity createGalaxyEntity(@NonNull JsonNode galaxyNode) {
        GalaxyEntity galaxyEntity = new GalaxyEntity();
        galaxyEntity.setKey(JsonParserHelper.stringFieldParse("key", galaxyNode));
        galaxyEntity.setName(JsonParserHelper.stringFieldParse("name", galaxyNode).toLowerCase());
        galaxyEntity.setDescripiton(JsonParserHelper.stringFieldParse("description", galaxyNode));
        return galaxyEntity;
    }
}
