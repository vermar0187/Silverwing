package com.rjdiscbots.silverwing.discord.message.event;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.rjdiscbots.silverwing.db.champions.ChampionStatsEntity;
import com.rjdiscbots.silverwing.db.champions.ChampionStatsRepository;
import com.rjdiscbots.silverwing.db.champions.ChampionsEntity;
import com.rjdiscbots.silverwing.db.champions.ChampionsRepository;
import com.rjdiscbots.silverwing.exceptions.message.EntityDoesNotExistException;
import com.rjdiscbots.silverwing.exceptions.message.InvalidMessageException;
import com.rjdiscbots.silverwing.exceptions.message.NoArgumentProvidedException;
import com.rjdiscbots.silverwing.utility.DiscordMessageHelper;
import java.util.Map;
import net.dv8tion.jda.api.EmbedBuilder;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

@Component
public class ChampionMessageEventHandler implements MessageEvent {

    private ChampionsRepository championsRepository;

    private ChampionStatsRepository championStatsRepository;

    private final Logger logger = LoggerFactory.getLogger(ChampionMessageEventHandler.class);

    @Autowired
    public ChampionMessageEventHandler(
        ChampionsRepository championsRepository,
        ChampionStatsRepository championStatsRepository) {
        this.championsRepository = championsRepository;
        this.championStatsRepository = championStatsRepository;
    }

    @Override
    public void handleEmbedMessage(@NonNull String rawChampionMessage,
        @NonNull EmbedBuilder embedBuilder, @NonNull Map<String, String> filePathMap)
        throws InvalidMessageException {
        if (!rawChampionMessage.startsWith("!champion")) {
            throw new IllegalArgumentException(
                "Message does not begin with !champion: " + rawChampionMessage);
        }

        String championName = rawChampionMessage.replaceFirst("!champion", "");
        championName = championName.trim();

        fetchChampion(championName, embedBuilder, filePathMap);
        fetchChampionStats(championName, embedBuilder);
    }

    private void fetchChampion(String championName, EmbedBuilder embedBuilder,
        Map<String, String> filePathMap) throws EntityDoesNotExistException,
        NoArgumentProvidedException {
        if (championName == null || StringUtils.isAllBlank(championName)) {
            throw new NoArgumentProvidedException("No champion provided!");
        }

        ChampionsEntity champion = championsRepository.findOneByName(championName);

        if (champion == null) {
            throw new EntityDoesNotExistException("Invalid champion provided!");
        }

        String formattedChampionName = DiscordMessageHelper.formatName(championName);

        String traits = DiscordMessageHelper.formatStringList(champion.getTraits());

        String ability = champion.getAbility();
        try {
            ability = DiscordMessageHelper.formatAbility(champion.getAbility());
        } catch (JsonProcessingException | NullPointerException e) {
            logger.info(String.format("Champion: %s, Ability: %s", formattedChampionName, ability),
                e.fillInStackTrace());
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
            throw new EntityDoesNotExistException("Invalid champion provided!");
        }

        embedBuilder.addField("Health", String.valueOf(championStats.getHealth()), true);
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
