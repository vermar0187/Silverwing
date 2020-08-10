package com.rjdiscbots.tftbot.scheduler.parser;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.rjdiscbots.tftbot.db.champions.ChampionStatsEntity;
import com.rjdiscbots.tftbot.db.champions.ChampionStatsRepository;
import com.rjdiscbots.tftbot.db.champions.ChampionsEntity;
import com.rjdiscbots.tftbot.db.champions.ChampionsRepository;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

@Component
public class ChampionsParser {

    private ChampionsRepository championsRepository;

    private ChampionStatsRepository championStatsRepository;

    private final Logger logger = LoggerFactory.getLogger(ChampionsParser.class);

    @Autowired
    public ChampionsParser(ChampionsRepository championsRepository,
        ChampionStatsRepository championStatsRepository) {
        this.championsRepository = championsRepository;
        this.championStatsRepository = championStatsRepository;
    }

    public void deserializeChampionInfo(JsonNode championNodes) {
        if (championNodes == null) {
            return;
        }

        Iterator<JsonNode> championIterator = championNodes.elements();

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
