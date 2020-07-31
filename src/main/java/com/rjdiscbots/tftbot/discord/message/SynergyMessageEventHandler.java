package com.rjdiscbots.tftbot.discord.message;

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
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SynergyMessageEventHandler {

    private SynergyRepository synergyRepository;

    private ChampionsRepository championsRepository;

    @Autowired
    public SynergyMessageEventHandler(SynergyRepository synergyRepository,
        ChampionsRepository championsRepository) {
        this.synergyRepository = synergyRepository;
        this.championsRepository = championsRepository;
    }

    public String handleSynergyMessage(String rawSynergyMessage)
        throws InvalidMessageException {
        if (!rawSynergyMessage.startsWith("!synergy ")) {
            throw new IllegalArgumentException(
                "Message does begin with !synergy: " + rawSynergyMessage);
        }

        String synergyName = rawSynergyMessage.replaceFirst("!synergy ", "");
        synergyName = synergyName.trim();

        StringBuilder returnMessage = new StringBuilder();

        String synergyMessage = fetchSynergy(synergyName);
        String championsBySynergyMessage = fetchChampionsBySynergy(synergyName);

        returnMessage.append(synergyMessage).append("\n").append(championsBySynergyMessage);

        return returnMessage.toString();
    }

    public String fetchSynergy(String synergyName) throws EntityDoesNotExistException {
        List<SynergyEntity> synergyList = synergyRepository.findByName(synergyName);

        if (synergyList.isEmpty()) {
            throw new EntityDoesNotExistException("Invalid synergy provided!");
        }

        StringBuilder synergyMessage = new StringBuilder();

        SynergyEntity synergy = synergyList.get(0);

        String formattedSynergyName = DiscordMessageHelper.formatName(synergyName);

        synergyMessage.append("__").append(formattedSynergyName).append("__").append("\n");
        synergyMessage.append("Description: ").append(synergy.getDescription()).append("\n");

        if (synergy.getInnate() != null) {
            synergyMessage.append("Innate Ability: ").append(synergy.getInnate()).append("\n");
        }

        if (synergy.getSetModel() != null) {
            synergyMessage.append("Medals: ");
            List<SetModel> model = synergy.getSetModel();
            for (int i = 0; i < model.size(); i++) {
                SetModel setModel = model.get(i);
                if (i != 0) {
                    synergyMessage.append(", ");
                }

                String style = setModel.getStyle();
                style = style.substring(0, 1).toUpperCase() + style.substring(1);
                Integer min = setModel.getMin();
                Integer max = setModel.getMax();

                synergyMessage.append(style).append(" (");

                if (min != null && max != null) {
                    synergyMessage.append(min).append("-").append(max).append(")");
                } else if (min != null) {
                    synergyMessage.append(min).append(")");
                } else if (max != null) {
                    synergyMessage.append(max).append(")");
                }
            }
        }

        return synergyMessage.toString();
    }

    public String fetchChampionsBySynergy(String synergyName) throws EntityDoesNotExistException {
        List<ChampionsEntity> championsEntityList = championsRepository
            .findByTrait(synergyName);

        if (championsEntityList == null || championsEntityList.isEmpty()) {
            throw new EntityDoesNotExistException("Invalid synergy provided!");
        }

        StringBuilder championsBySynergyMessage = new StringBuilder();

        championsEntityList = championsEntityList.stream()
            .sorted((Comparator.comparingInt(ChampionsEntity::getCost)))
            .collect(Collectors.toList());

        championsBySynergyMessage.append("Champions: ");
        for (int i = 0; i < championsEntityList.size(); i++) {
            ChampionsEntity championsEntity = championsEntityList.get(i);
            if (i != 0) {
                championsBySynergyMessage.append(", ");
            }

            String formattedChampionName = DiscordMessageHelper
                .formatName(championsEntity.getName());
            championsBySynergyMessage.append(formattedChampionName).append(" (")
                .append(championsEntity.getCost()).append(")");
        }

        return championsBySynergyMessage.toString();
    }
}
