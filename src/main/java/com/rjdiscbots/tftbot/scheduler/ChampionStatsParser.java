package com.rjdiscbots.tftbot.scheduler;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rjdiscbots.tftbot.config.DiscordConfig;
import com.rjdiscbots.tftbot.db.champions.ChampionStatsEntity;
import com.rjdiscbots.tftbot.db.champions.ChampionStatsRepository;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ChampionStatsParser {

    private ChampionStatsRepository championStatsRepository;
    private DiscordConfig discordConfig;

    @Autowired
    public ChampionStatsParser(ChampionStatsRepository championStatsRepository,
        DiscordConfig discordConfig) {
        this.championStatsRepository = championStatsRepository;
        this.discordConfig = discordConfig;
    }

    @PostConstruct
    public void onStartup() {
        if (discordConfig.isProduction()) {
            loadPatch();
        }
    }

    private void loadPatch() {
        ObjectMapper objectMapper = new ObjectMapper();
        File latestPatch = new File("patch/en_us.json");

        JsonNode jsonNode;
        try {
            jsonNode = objectMapper.readTree(latestPatch);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        JsonNode championNodes = jsonNode.at("/sets/3/champions");

        deserializeChampionStats(championNodes);
    }

    private void deserializeChampionStats(JsonNode championNodes) {
        if (championNodes == null) {
            return;
        }

        Iterator<JsonNode> championIterator = championNodes.elements();

        while (championIterator.hasNext()) {
            JsonNode championNode = championIterator.next();
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

            championStatsRepository.save(championStatsEntity);
        }
    }
}
