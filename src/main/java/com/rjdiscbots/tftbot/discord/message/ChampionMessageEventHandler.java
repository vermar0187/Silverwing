package com.rjdiscbots.tftbot.discord.message;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.rjdiscbots.tftbot.config.DiscordConfig;
import com.rjdiscbots.tftbot.db.champions.ChampionStatsEntity;
import com.rjdiscbots.tftbot.db.champions.ChampionStatsRepository;
import com.rjdiscbots.tftbot.db.champions.ChampionsEntity;
import com.rjdiscbots.tftbot.db.champions.ChampionsRepository;
import com.rjdiscbots.tftbot.utility.DiscordMessageHelper;
import java.util.List;
import net.dv8tion.jda.api.EmbedBuilder;
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

    public String handleChampionMessage(String rawChampionMessage) {
        String championMessage = rawChampionMessage.replaceFirst("!champion ", "");
        championMessage = championMessage.trim();

        List<ChampionsEntity> champions = championsRepository.findByName(championMessage);
        List<ChampionStatsEntity> championStats = championStatsRepository
            .findByChampionOrderByStarsAsc(championMessage);

        if (champions == null || champions.isEmpty() || championStats == null ||
            championStats.isEmpty()) {
            return "No such champion exists!";
        }

        ChampionsEntity champion = champions.get(0);
        String championName = DiscordMessageHelper.formatName(champion.getName());

        StringBuilder returnMessage = new StringBuilder();

        returnMessage.append("__").append(championName).append("__").append("\n");
        returnMessage.append("Cost: ").append(champion.getCost()).append("\n");
        returnMessage.append("Traits: ");
        List<String> traits = champion.getTraits();
        for (int i = 0; i < traits.size(); i++) {
            String trait = DiscordMessageHelper.formatName(traits.get(i));
            if (i != 0) {
                returnMessage.append(", ");
            }
            returnMessage.append(trait);
        }
        returnMessage.append("\n\n");

        try {
            returnMessage.append(DiscordMessageHelper.formatAbility(champion.getAbility()));
        } catch (JsonProcessingException e) {
            System.out.println("Unable to parse ability");
        }
        returnMessage.append("\n\n");

        ChampionStatsEntity championStatsEntity = championStats.get(0);
        returnMessage.append("DPS: ").append(championStatsEntity.getDps()).append("\n");
        returnMessage.append("Damage: ").append(championStatsEntity.getDamage()).append("\n");
        Double attackSpeed = DiscordMessageHelper
            .formatDouble(championStatsEntity.getAttackSpeed());
        returnMessage.append("Attack Speed: ").append(attackSpeed).append("\n");
        returnMessage.append("Range: ").append(championStatsEntity.getRange()).append("\n");
        returnMessage.append("Mana Total: ").append(championStatsEntity.getInitialMana())
            .append("/").append(championStatsEntity.getMana()).append("\n");
        returnMessage.append("Armor: ").append(championStatsEntity.getArmor()).append("\n");
        returnMessage.append("Magic Resist: ").append(championStatsEntity.getMr()).append("\n\n");

        return returnMessage.toString();
    }
}
