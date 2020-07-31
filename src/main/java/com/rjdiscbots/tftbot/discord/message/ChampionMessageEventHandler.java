package com.rjdiscbots.tftbot.discord.message;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.rjdiscbots.tftbot.db.champions.ChampionStatsEntity;
import com.rjdiscbots.tftbot.db.champions.ChampionStatsRepository;
import com.rjdiscbots.tftbot.db.champions.ChampionsEntity;
import com.rjdiscbots.tftbot.db.champions.ChampionsRepository;
import com.rjdiscbots.tftbot.exceptions.message.EntityDoesNotExistException;
import com.rjdiscbots.tftbot.exceptions.message.InvalidMessageException;
import com.rjdiscbots.tftbot.utility.DiscordMessageHelper;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ChampionMessageEventHandler {

    private ChampionsRepository championsRepository;
    private ChampionStatsRepository championStatsRepository;

    @Autowired
    public ChampionMessageEventHandler(
        ChampionsRepository championsRepository,
        ChampionStatsRepository championStatsRepository) {
        this.championsRepository = championsRepository;
        this.championStatsRepository = championStatsRepository;
    }

    public String handleChampionMessage(String rawChampionMessage) throws InvalidMessageException {
        if (!rawChampionMessage.startsWith("!champion ")) {
            throw new IllegalArgumentException(
                "Message does begin with !champion: " + rawChampionMessage);
        }

        String championMessage = rawChampionMessage.replaceFirst("!champion ", "");
        championMessage = championMessage.trim();

        StringBuilder returnMessage = new StringBuilder();

        String championInfo = fetchChampion(championMessage);
        String championStatsInfo = fetchChampionStats(championMessage);

        returnMessage.append(championInfo).append("\n\n").append(championStatsInfo);

        return returnMessage.toString();
    }

    private String fetchChampion(String championName) throws EntityDoesNotExistException {
        List<ChampionsEntity> champions = championsRepository.findByName(championName);

        if (champions == null || champions.isEmpty()) {
            throw new EntityDoesNotExistException("Invalid champion provided!");
        }

        ChampionsEntity champion = champions.get(0);

        championName = DiscordMessageHelper.formatName(champion.getName());

        StringBuilder championMessage = new StringBuilder();

        championMessage.append("__").append(championName).append("__").append("\n");
        championMessage.append("Cost: ").append(champion.getCost()).append("\n");
        championMessage.append("Traits: ");
        List<String> traits = champion.getTraits();
        for (int i = 0; i < traits.size(); i++) {
            String trait = DiscordMessageHelper.formatName(traits.get(i));
            if (i != 0) {
                championMessage.append(", ");
            }
            championMessage.append(trait);
        }

        championMessage.append("\n\n");

        try {
            championMessage.append(DiscordMessageHelper.formatAbility(champion.getAbility()));
        } catch (JsonProcessingException e) {
            System.out.println("Unable to parse ability");
        }

        return championMessage.toString();
    }

    private String fetchChampionStats(String championName) throws EntityDoesNotExistException {
        List<ChampionStatsEntity> championStats = championStatsRepository
            .findByChampionOrderByStarsAsc(championName);

        if (championStats == null || championStats.isEmpty()) {
            throw new EntityDoesNotExistException("Invalid champion provided!");
        }

        StringBuilder championStatsMessage = new StringBuilder();

        ChampionStatsEntity championStatsEntity = championStats.get(0);
        championStatsMessage.append("DPS: ").append(championStatsEntity.getDps()).append("\n");
        championStatsMessage.append("Damage: ").append(championStatsEntity.getDamage())
            .append("\n");
        Double attackSpeed = DiscordMessageHelper
            .formatDouble(championStatsEntity.getAttackSpeed());
        championStatsMessage.append("Attack Speed: ").append(attackSpeed).append("\n");
        championStatsMessage.append("Range: ").append(championStatsEntity.getRange()).append("\n");
        championStatsMessage.append("Mana Total: ").append(championStatsEntity.getInitialMana())
            .append("/").append(championStatsEntity.getMana()).append("\n");
        championStatsMessage.append("Armor: ").append(championStatsEntity.getArmor()).append("\n");
        championStatsMessage.append("Magic Resist: ").append(championStatsEntity.getMr());

        return championStatsMessage.toString();
    }
}
