package com.rjdiscbots.tftbot.discord.message;

import com.rjdiscbots.tftbot.db.champions.ChampionsEntity;
import com.rjdiscbots.tftbot.db.champions.ChampionsRepository;
import com.rjdiscbots.tftbot.db.synergies.SetModel;
import com.rjdiscbots.tftbot.db.synergies.SynergyEntity;
import com.rjdiscbots.tftbot.db.synergies.SynergyRepository;
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

    public String handleSynergyMessage(String rawSynergyMessage) {
        String synergyName = rawSynergyMessage.replaceFirst("!synergy ", "");
        synergyName = synergyName.trim();

        List<SynergyEntity> synergyList = synergyRepository.findByName(synergyName);

        if (synergyList.isEmpty()) {
            return "No such synergy exists!";
        }

        SynergyEntity synergy = synergyList.get(0);

        StringBuilder returnMessage = new StringBuilder();

        returnMessage.append(DiscordMessageHelper.formatName(synergyName)).append("\n");
        returnMessage.append("Description: ").append(synergy.getDescription()).append("\n");

        if (synergy.getInnate() != null) {
            returnMessage.append("Innate Ability: ").append(synergy.getInnate()).append("\n");
        }

        if (synergy.getSetModel() != null) {
            returnMessage.append("Medals: ");
            List<SetModel> model = synergy.getSetModel();
            for (int i = 0; i < model.size(); i++) {
                SetModel setModel = model.get(i);
                if (i != 0) {
                    returnMessage.append(", ");
                }

                String style = setModel.getStyle();
                style = style.substring(0, 1).toUpperCase() + style.substring(1);
                Integer min = setModel.getMin();
                Integer max = setModel.getMax();

                returnMessage.append(style).append(" (");

                if (min != null && max != null) {
                    returnMessage.append(min).append("-").append(max).append(")");
                } else if (min != null) {
                    returnMessage.append(min).append(")");
                } else if (max != null) {
                    returnMessage.append(max).append(")");
                }
            }
        }

        List<ChampionsEntity> championsEntityList = championsRepository
            .findByTrait(synergyName);

        if (championsEntityList != null) {
            championsEntityList = championsEntityList.stream()
                .sorted((Comparator.comparingInt(ChampionsEntity::getCost)))
                .collect(Collectors.toList());

            returnMessage.append("\nChampions: ");
            for (int i = 0; i < championsEntityList.size(); i++) {
                ChampionsEntity championsEntity = championsEntityList.get(i);
                if (i != 0) {
                    returnMessage.append(", ");
                }

                String championName = DiscordMessageHelper
                    .formatName(championsEntity.getName());
                returnMessage.append(championName).append(" (")
                    .append(championsEntity.getCost()).append(")");
            }
        }

        return returnMessage.toString();
    }
}
