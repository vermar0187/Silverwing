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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ChampionsParser {

    private ChampionsRepository championsRepository;
    private ChampionStatsRepository championStatsRepository;

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
            ChampionStatsEntity championStatsEntity = new ChampionStatsEntity();
            ChampionsEntity championsEntity = new ChampionsEntity();

            String championName = championNode.get("name").asText().toLowerCase();
            String championId = championNode.get("apiName").asText();
            List<String> championTraits = new ArrayList<>();
            JsonNode traitNodes = championNode.get("traits");

            if (traitNodes.isArray()) {
                ArrayNode traitNodeArray = (ArrayNode) traitNodes;
                for (JsonNode traitNode : traitNodeArray) {
                    championTraits.add(traitNode.asText());
                }
            }

            championsEntity.setCost(championNode.get("cost").asInt());
            championsEntity.setId(championId);
            championsEntity.setName(championName);
            championsEntity.setTraits(championTraits);
            championsEntity.setAbility(championNode.get("ability").toString());

            championStatsEntity.setId(championId);
            championStatsEntity.setChampion(championName);
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

            try {
                championsRepository.save(championsEntity);
                championStatsRepository.save(championStatsEntity);
            } catch (Exception e) {
                System.out.println(championId);
                System.out.println(championsEntity.getAbility());
            }
        }
    }
}
