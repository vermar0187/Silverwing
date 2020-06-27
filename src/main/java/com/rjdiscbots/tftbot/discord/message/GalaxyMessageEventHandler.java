package com.rjdiscbots.tftbot.discord.message;

import com.rjdiscbots.tftbot.db.galaxies.GalaxiesEntity;
import com.rjdiscbots.tftbot.db.galaxies.GalaxiesRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class GalaxyMessageEventHandler {

    private GalaxiesRepository galaxiesRepository;

    @Autowired
    public GalaxyMessageEventHandler(GalaxiesRepository galaxiesRepository) {
        this.galaxiesRepository = galaxiesRepository;
    }

    public String handleGalaxyMessage(String rawGalaxyMessage) {
        rawGalaxyMessage = rawGalaxyMessage.replaceFirst("!galaxy ", "");
        rawGalaxyMessage = rawGalaxyMessage.replaceAll("\"", "");

        List<GalaxiesEntity> galaxiesEntity = galaxiesRepository.findByName(rawGalaxyMessage);

        if (galaxiesEntity.isEmpty()) {
            return "No such galaxy exists!";
        } else {
            return galaxiesEntity.get(0).getDescripiton();
        }
    }

    public String handleListGalaxyMessage() {
        List<GalaxiesEntity> galaxiesEntities = galaxiesRepository.findAll();
        StringBuilder allGalaxies = new StringBuilder();

        for (int i = 0; i < galaxiesEntities.size(); i++) {
            if (i == 0) {
                allGalaxies.append(galaxiesEntities.get(i).getName());
            } else {
                allGalaxies.append("\n").append(galaxiesEntities.get(i).getName());
            }
        }

        return allGalaxies.toString();
    }
}
