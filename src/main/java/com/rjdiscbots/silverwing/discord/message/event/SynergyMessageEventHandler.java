package com.rjdiscbots.silverwing.discord.message.event;

import com.rjdiscbots.silverwing.db.champions.ChampionsEntity;
import com.rjdiscbots.silverwing.db.champions.ChampionsRepository;
import com.rjdiscbots.silverwing.db.synergies.SynergyEntity;
import com.rjdiscbots.silverwing.db.synergies.SynergyRepository;
import com.rjdiscbots.silverwing.exceptions.message.EntityDoesNotExistException;
import com.rjdiscbots.silverwing.exceptions.message.InvalidMessageException;
import com.rjdiscbots.silverwing.exceptions.message.NoArgumentProvidedException;
import com.rjdiscbots.silverwing.utility.DiscordMessageHelper;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import net.dv8tion.jda.api.EmbedBuilder;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

@Component
public class SynergyMessageEventHandler implements MessageEvent {

    private SynergyRepository synergyRepository;

    private ChampionsRepository championsRepository;

    @Autowired
    public SynergyMessageEventHandler(SynergyRepository synergyRepository,
        ChampionsRepository championsRepository) {
        this.synergyRepository = synergyRepository;
        this.championsRepository = championsRepository;
    }

    @Override
    public void handleEmbedMessage(@NonNull String rawSynergyMessage,
        @NonNull EmbedBuilder embedBuilder, @NonNull Map<String, String> filePathMap)
        throws InvalidMessageException {
        if (!rawSynergyMessage.startsWith("!synergy")) {
            throw new IllegalArgumentException(
                "Message does not begin with !synergy: " + rawSynergyMessage);
        }

        String synergyName = rawSynergyMessage.replaceFirst("!synergy", "");
        synergyName = synergyName.trim();

        fetchSynergy(synergyName, embedBuilder, filePathMap);
        fetchChampionsBySynergy(synergyName, embedBuilder);
    }

    private void fetchSynergy(String synergyName, EmbedBuilder embedBuilder,
        Map<String, String> filePathMap) throws EntityDoesNotExistException,
        NoArgumentProvidedException {
        if (synergyName == null || StringUtils.isAllBlank(synergyName)) {
            throw new NoArgumentProvidedException("No synergy provided!");
        }

        SynergyEntity synergy = synergyRepository.findOneByName(synergyName);

        if (synergy == null) {
            throw new EntityDoesNotExistException("Invalid synergy provided!");
        }

        String formattedSynergyName = DiscordMessageHelper.formatName(synergyName);

        String picUrl = synergyName.replaceAll(" ", "").toLowerCase() + ".png";
        filePathMap.put(picUrl, "patch/traits/" + picUrl);

        embedBuilder.setTitle(formattedSynergyName);
        embedBuilder.setDescription(synergy.getDescription());
        embedBuilder.setThumbnail("attachment://" + picUrl);
        if (synergy.getInnate() != null && !synergy.getInnate().isEmpty()) {
            embedBuilder.addField("Innate Ability", synergy.getInnate(), false);
        }
        if (synergy.getSetModel() != null) {
            embedBuilder.addField("Medals",
                DiscordMessageHelper.formatSetModel(synergy.getSetModel()), false);
        }

    }

    private void fetchChampionsBySynergy(String synergyName, EmbedBuilder embedBuilder)
        throws EntityDoesNotExistException {
        List<ChampionsEntity> championsEntityList = championsRepository
            .findByTrait(synergyName);

        if (championsEntityList == null || championsEntityList.isEmpty()) {
            throw new EntityDoesNotExistException("Invalid synergy provided!");
        }

        StringBuilder championInSynergy = new StringBuilder();

        List<String> champions = championsEntityList.stream()
            .sorted((Comparator.comparingInt(ChampionsEntity::getCost)))
            .map((championsEntity -> DiscordMessageHelper.formatName(championsEntity.getName())
                + " (" + championsEntity.getCost() + ")"))
            .collect(Collectors.toList());

        championInSynergy.append(DiscordMessageHelper.formatStringList(champions));

        embedBuilder.addField("Champions", championInSynergy.toString(), false);
    }
}
