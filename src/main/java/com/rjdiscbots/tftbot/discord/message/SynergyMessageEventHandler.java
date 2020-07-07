package com.rjdiscbots.tftbot.discord.message;

import com.rjdiscbots.tftbot.db.synergies.SetModel;
import com.rjdiscbots.tftbot.db.synergies.SynergyEntity;
import com.rjdiscbots.tftbot.db.synergies.SynergyRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SynergyMessageEventHandler {

    private SynergyRepository synergyRepository;

    @Autowired
    public SynergyMessageEventHandler(SynergyRepository synergyRepository) {
        this.synergyRepository = synergyRepository;
    }

    public String handleBuildMessage(String rawSynergyMessage) {
        String synergyName = rawSynergyMessage.replaceFirst("!synergy ", "");
        synergyName = synergyName.trim();

        List<SynergyEntity> synergyList = synergyRepository.findByName(synergyName);

        if (synergyList.isEmpty()) {
            return "No such synergy exists!";
        }

        SynergyEntity synergy = synergyList.get(0);

        StringBuilder returnMessage = new StringBuilder();

        synergyName = synergyName.substring(0, 1).toUpperCase() + synergyName.substring(1);

        returnMessage.append(synergyName).append("\n");
        returnMessage.append("Description: ").append(synergy.getDescription()).append("\n");

        if (synergy.getInnate() != null) {
            returnMessage.append("Innate Ability: ").append(synergy.getInnate()).append("\n");
        }

        if (synergy.getSetModel() != null) {
            for (SetModel setModel : synergy.getSetModel()) {
                String style = setModel.getStyle();
                style = style.substring(0, 1).toUpperCase() + style.substring(1);
                Integer min = setModel.getMin();
                Integer max = setModel.getMax();

                returnMessage.append(style).append(" (");

                if (min != null && max != null) {
                    returnMessage.append(min).append("-").append(max).append(")\n");
                } else if (min != null) {
                    returnMessage.append(min).append(")\n");
                } else if (max != null) {
                    returnMessage.append(max).append(")\n");
                }
            }
        }

        return returnMessage.toString();
    }
}
