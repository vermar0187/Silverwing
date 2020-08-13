package com.rjdiscbots.tftbot.scheduler.parser;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rjdiscbots.tftbot.config.DiscordConfig;
import com.rjdiscbots.tftbot.exceptions.parser.PatchProcessingException;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import javax.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PatchParser {

    private ChampionsSerde championParser;

    private DiscordConfig discordConfig;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final Logger logger = LoggerFactory.getLogger(PatchParser.class);

    @Autowired
    public PatchParser(ChampionsSerde championParser,
        DiscordConfig discordConfig) {
        this.championParser = championParser;
        this.discordConfig = discordConfig;
    }

    @PostConstruct
    public void onStartup() throws IOException, PatchProcessingException {
        if (discordConfig.isProduction()) {
            updatePatch();
        }
    }

    public void updatePatch() throws IOException, PatchProcessingException {
        File oldPatch = new File("patch/en_us.json");
        URL patchURL = new URL(discordConfig.getPatch());

        JsonNode oldPatchNode;
        JsonNode newPatchNode;

        oldPatchNode = objectMapper.readTree(oldPatch);
        newPatchNode = objectMapper.readTree(patchURL);

        JsonNode oldChampNode = oldPatchNode.at("/sets/3/champions");
        JsonNode newChampNode = newPatchNode.at("/sets/3/champions");

        try {
            oldChampNode = championParser.serializeChampions(oldChampNode, newChampNode);
        } catch (PatchProcessingException e) {
            logger.warn("Patch could not be parsed properly. Will use old patch", e);
        } finally {
            championParser.deserializeChampionInfo(oldChampNode);
        }
    }
}
