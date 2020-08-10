package com.rjdiscbots.tftbot.discord.message.event;

import com.rjdiscbots.tftbot.db.champions.ChampionsEntity;
import com.rjdiscbots.tftbot.db.champions.ChampionsRepository;
import com.rjdiscbots.tftbot.db.synergies.SetModel;
import com.rjdiscbots.tftbot.db.synergies.SynergyEntity;
import com.rjdiscbots.tftbot.db.synergies.SynergyRepository;
import com.rjdiscbots.tftbot.exceptions.message.EntityDoesNotExistException;
import com.rjdiscbots.tftbot.exceptions.message.InvalidMessageException;
import com.rjdiscbots.tftbot.utility.DiscordMessageHelper;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import net.dv8tion.jda.api.EmbedBuilder;
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
        if (!rawSynergyMessage.startsWith("!synergy ")) {
            throw new IllegalArgumentException(
                "Message does begin with !synergy: " + rawSynergyMessage);
        }

        String synergyName = rawSynergyMessage.replaceFirst("!synergy ", "");
        synergyName = synergyName.trim();

        fetchSynergy(synergyName, embedBuilder, filePathMap);
        fetchChampionsBySynergy(synergyName, embedBuilder);
    }

    private void fetchSynergy(String synergyName, EmbedBuilder embedBuilder,
        Map<String, String> filePathMap) throws InvalidMessageException {
        SynergyEntity synergy = synergyRepository.findOneByName(synergyName);

        if (synergy == null) {
            throw new EntityDoesNotExistException("Invalid synergy provided!");
        }

        String formattedSynergyName = DiscordMessageHelper.formatName(synergyName);

        String picUrl = synergyName.replaceAll(" ", "").toLowerCase() + ".png";
        filePathMap.put(picUrl, "patch/traits/" + picUrl);

        StringBuilder medal = new StringBuilder();
        if (synergy.getSetModel() != null) {
            List<SetModel> model = synergy.getSetModel();
            for (int i = 0; i < model.size(); i++) {
                SetModel setModel = model.get(i);
                if (i != 0) {
                    medal.append(", ");
                }

                String style = setModel.getStyle();
                style = style.substring(0, 1).toUpperCase() + style.substring(1);
                Integer min = setModel.getMin();
                Integer max = setModel.getMax();

                medal.append(style).append(" (");

                if (min != null && max != null) {
                    medal.append(min).append("-").append(max).append(")");
                } else if (min != null) {
                    medal.append(min).append(")");
                } else if (max != null) {
                    medal.append(max).append(")");
                }
            }
        }

        embedBuilder.setTitle(formattedSynergyName);
        embedBuilder.setDescription(synergy.getDescription());
        embedBuilder.setThumbnail("attachment://" + picUrl);
        if (synergy.getInnate() != null && !synergy.getInnate().isEmpty()) {
            embedBuilder.addField("Innate Ability", synergy.getInnate(), false);
        }
        embedBuilder.addField("Medals", medal.toString(), false);
    }

    private void fetchChampionsBySynergy(String synergyName, EmbedBuilder embedBuilder)
        throws EntityDoesNotExistException {
        List<ChampionsEntity> championsEntityList = championsRepository
            .findByTrait(synergyName);

        if (championsEntityList == null || championsEntityList.isEmpty()) {
            throw new EntityDoesNotExistException("Invalid synergy provided!");
        }

        StringBuilder championInSynergy = new StringBuilder();

        championsEntityList = championsEntityList.stream()
            .sorted((Comparator.comparingInt(ChampionsEntity::getCost)))
            .collect(Collectors.toList());

        for (int i = 0; i < championsEntityList.size(); i++) {
            ChampionsEntity championsEntity = championsEntityList.get(i);
            if (i != 0) {
                championInSynergy.append(", ");
            }

            String formattedChampionName = DiscordMessageHelper
                .formatName(championsEntity.getName());
            championInSynergy.append(formattedChampionName).append(" (")
                .append(championsEntity.getCost()).append(")");
        }

        embedBuilder.addField("Champions", championInSynergy.toString(), false);
    }
}
