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
import java.util.Map;
import net.dv8tion.jda.api.EmbedBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
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

    public void handleEmbedChampionMessage(@NonNull String rawChampionMessage,
        @NonNull EmbedBuilder embedBuilder, @NonNull Map<String, String> filePathMap)
        throws InvalidMessageException {
        if (!rawChampionMessage.startsWith("!champion ")) {
            throw new IllegalArgumentException(
                "Message does begin with !champion: " + rawChampionMessage);
        }

        String championMessage = rawChampionMessage.replaceFirst("!champion ", "");
        championMessage = championMessage.trim();

        fetchChampion(championMessage, embedBuilder, filePathMap);
        fetchChampionStats(championMessage, embedBuilder);
    }

    private void fetchChampion(String championName, EmbedBuilder embedBuilder,
        Map<String, String> filePathMap) throws EntityDoesNotExistException {

        ChampionsEntity champion = championsRepository.findOneByName(championName);

        if (champion == null) {
            throw new EntityDoesNotExistException("Invalid champion provided!");
        }

        String formattedChampionName = DiscordMessageHelper.formatName(championName);

        String traits = "";
        List<String> traitList = champion.getTraits();
        for (int i = 0; i < traitList.size(); i++) {
            String trait = DiscordMessageHelper.formatName(traitList.get(i));
            if (i != 0) {
                traits = traits.concat(", ");
            }
            traits = traits.concat(trait);
        }

        String ability = "";
        try {
            ability = DiscordMessageHelper.formatAbility(champion.getAbility());
        } catch (JsonProcessingException e) {
            System.out.println("Unable to parse ability");
            ability = "Not found";
        }

        String picUrl = championName.replaceAll(" ", "").toLowerCase() + ".png";
        filePathMap.put(picUrl, "patch/champions/" + picUrl);

        embedBuilder.setTitle(formattedChampionName);
        embedBuilder.setThumbnail("attachment://" + picUrl);
        embedBuilder.setDescription(ability);
        embedBuilder.addField("Cost", String.valueOf(champion.getCost()), true);
        embedBuilder.addField("Traits", traits, true);
    }

    private void fetchChampionStats(String championName, EmbedBuilder embedBuilder)
        throws EntityDoesNotExistException {
        ChampionStatsEntity championStats = championStatsRepository
            .findOneByChampionOrderByStarsAsc(championName);

        if (championStats == null) {
            throw new EntityDoesNotExistException("Invalid champion provided");
        }

        embedBuilder.addField("DPS", String.valueOf(championStats.getDps()), true);
        embedBuilder.addField("Damage", String.valueOf(championStats.getDamage()), true);

        Double attackSpeed = DiscordMessageHelper.formatDouble(championStats.getAttackSpeed());
        embedBuilder.addField("Attack Speed", String.valueOf(attackSpeed), true);

        embedBuilder.addField("Range", String.valueOf(championStats.getRange()), true);

        String mana = String.valueOf(championStats.getInitialMana()).concat("/")
            .concat(String.valueOf(championStats.getMana()));
        embedBuilder.addField("Mana Total", mana, true);

        embedBuilder.addField("Armor", String.valueOf(championStats.getArmor()), true);
        embedBuilder.addField("Magic Resist", String.valueOf(championStats.getMr()), true);
    }
}
