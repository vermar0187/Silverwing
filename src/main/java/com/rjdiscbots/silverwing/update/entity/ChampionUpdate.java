package com.rjdiscbots.silverwing.update.entity;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.rjdiscbots.silverwing.config.DiscordConfig;
import com.rjdiscbots.silverwing.db.champions.ChampionStatsEntity;
import com.rjdiscbots.silverwing.db.champions.ChampionStatsRepository;
import com.rjdiscbots.silverwing.db.champions.ChampionsEntity;
import com.rjdiscbots.silverwing.db.champions.ChampionsRepository;
import com.rjdiscbots.silverwing.exceptions.parser.JsonFieldDoesNotExistException;
import com.rjdiscbots.silverwing.exceptions.parser.PatchProcessingException;
import com.rjdiscbots.silverwing.update.UpdateEntity;
import com.rjdiscbots.silverwing.utility.JsonParserHelper;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

@Component
public class ChampionUpdate implements UpdateEntity {

    private ChampionsRepository championsRepository;

    private ChampionStatsRepository championStatsRepository;

    private final Logger logger = LoggerFactory.getLogger(ChampionUpdate.class);

    private DiscordConfig discordConfig;

    @Autowired
    public ChampionUpdate(ChampionsRepository championsRepository,
        ChampionStatsRepository championStatsRepository, DiscordConfig discordConfig) {
        this.championsRepository = championsRepository;
        this.championStatsRepository = championStatsRepository;
        this.discordConfig = discordConfig;
    }

    @Override
    public void patch() throws PatchProcessingException, IOException {
        File oldPatch = new File("patch/en_us.json");
        URL patchURL = new URL(discordConfig.getPatch());

        JsonNode oldChampNode = objectMapper.readTree(oldPatch).at("/sets/3/champions");
        JsonNode newChampNode = objectMapper.readTree(patchURL).at("/sets/3/champions");

        try {
            oldChampNode = update(oldChampNode, newChampNode);
        } catch (PatchProcessingException e) {
            throw new PatchProcessingException(
                String.format("Could not use patch url: %s. Will save using current patch.",
                    discordConfig.getPatch()));
        } finally {
            save(oldChampNode);
        }
    }

    @Override
    public JsonNode update(@NonNull JsonNode oldChampions,
        @NonNull JsonNode updatedChampions) throws JsonFieldDoesNotExistException {
        if (oldChampions.isMissingNode() || updatedChampions.isMissingNode()) {
            throw new JsonFieldDoesNotExistException("Champions do not exist");
        }

        Map<String, List<JsonNode>> oldToNewChamps = JsonParserHelper
            .matchingJsonNodesByFieldValue(oldChampions,
                updatedChampions, "apiName");

        for (Map.Entry<String, List<JsonNode>> entry : oldToNewChamps.entrySet()) {
            List<JsonNode> jsonNodes = entry.getValue();
            if (jsonNodes.size() != 2) {
                continue;
            }
            JsonNode oldChamp = jsonNodes.get(0);
            JsonNode newChamp = jsonNodes.get(1);

            if (oldChamp.path("ability").path("variables").isMissingNode() || newChamp
                .path("ability").path("variables").isMissingNode()) {
                throw new JsonFieldDoesNotExistException("Variables for champion does not exist");
            }

            JsonNode oldVariables = oldChamp.path("ability").path("variables");
            JsonNode newVariables = newChamp.path("ability").path("variables");

            Map<String, List<JsonNode>> oldVarsToNewVars = JsonParserHelper
                .matchingJsonNodesByFieldValue(oldVariables,
                    newVariables, "name");

            for (Map.Entry<String, List<JsonNode>> var : oldVarsToNewVars.entrySet()) {
                ObjectNode objectNode1 = (ObjectNode) var.getValue().get(0);
                ObjectNode objectNode2 = (ObjectNode) var.getValue().get(1);
                objectNode1.replace("value", objectNode2.get("value"));
            }
        }

        return oldChampions;
    }

    @Override
    public void save(@NonNull JsonNode champions) throws JsonFieldDoesNotExistException {
        if (champions.isMissingNode()) {
            throw new JsonFieldDoesNotExistException("Champion set does not exist");
        }

        Iterator<JsonNode> championIterator = champions.elements();

        while (championIterator.hasNext()) {
            JsonNode championNode = championIterator.next();

            ChampionsEntity championsEntity = null;
            ChampionStatsEntity championStatsEntity = null;

            try {
                championsEntity = createChampionsEntity(championNode);
                championStatsEntity = createChampionStatsEntity(championNode);
            } catch (Exception e) {
                logger.info(e.getMessage(), e.fillInStackTrace());
                continue;
            }

            try {
                championsRepository.save(championsEntity);
                championStatsRepository.save(championStatsEntity);
            } catch (Exception e) {
                logger.warn(e.getMessage(), e.fillInStackTrace());
            }
        }
    }

    private ChampionsEntity createChampionsEntity(@NonNull JsonNode championNode) {
        ChampionsEntity championsEntity = new ChampionsEntity();

        List<String> championTraits = new ArrayList<>();
        JsonNode traitNodes = championNode.get("traits");

        if (traitNodes.isArray()) {
            ArrayNode traitNodeArray = (ArrayNode) traitNodes;
            for (JsonNode traitNode : traitNodeArray) {
                championTraits.add(traitNode.asText().toLowerCase());
            }
        }

        championsEntity.setCost(championNode.get("cost").asInt());
        championsEntity.setId(championNode.get("apiName").asText());
        championsEntity.setName(championNode.get("name").asText().toLowerCase());
        championsEntity.setTraits(championTraits);
        championsEntity.setAbility(championNode.get("ability").toString());

        return championsEntity;
    }

    private ChampionStatsEntity createChampionStatsEntity(@NonNull JsonNode championNode) {
        ChampionStatsEntity championStatsEntity = new ChampionStatsEntity();

        championStatsEntity.setId(championNode.get("apiName").asText());
        championStatsEntity.setChampion(championNode.get("name").asText().toLowerCase());
        championStatsEntity.setStars(1);

        JsonNode championStatsNode = championNode.get("stats");
        double attackSpeed = championStatsNode.get("attackSpeed").asDouble();
        int damage = championStatsNode.get("damage").asInt();

        int dps = (int) (damage * attackSpeed);
        championStatsEntity.setDps(dps);
        championStatsEntity.setAttackSpeed(attackSpeed);
        championStatsEntity.setRange(championStatsNode.get("range").asInt());
        championStatsEntity.setDamage(damage);
        championStatsEntity.setHealth(championStatsNode.get("hp").asInt());
        championStatsEntity.setMana(championStatsNode.get("mana").asInt());
        championStatsEntity.setInitialMana(championStatsNode.get("initialMana").asInt());
        championStatsEntity.setArmor(championStatsNode.get("armor").asInt());
        championStatsEntity.setMr(championStatsNode.get("magicResist").asInt());

        return championStatsEntity;
    }

}
