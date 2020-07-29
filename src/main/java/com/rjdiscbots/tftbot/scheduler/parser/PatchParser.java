package com.rjdiscbots.tftbot.scheduler.parser;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rjdiscbots.tftbot.config.DiscordConfig;
import com.rjdiscbots.tftbot.db.champions.ChampionStatsRepository;
import java.io.File;
import java.io.IOException;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PatchParser {

    private ChampionsParser championParser;
    private DiscordConfig discordConfig;

    @Autowired
    public PatchParser(ChampionsParser championParser,
        DiscordConfig discordConfig) {
        this.championParser = championParser;
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
        championParser.deserializeChampionInfo(championNodes);
    }
}
