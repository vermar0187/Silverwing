package com.rjdiscbots.tftbot.discord.message;

import com.rjdiscbots.tftbot.db.galaxies.GalaxiesRepository;
import com.rjdiscbots.tftbot.db.galaxies.GalaxyEntity;
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
        rawGalaxyMessage = rawGalaxyMessage.trim();

        return fetchGalaxyDescription(rawGalaxyMessage);
    }

    private String fetchGalaxyDescription(String galaxy) {
        List<GalaxyEntity> galaxyEntity = galaxiesRepository.findByName(galaxy);

        if (galaxyEntity.isEmpty()) {
            return "No such galaxy exists!";
        } else {
            return galaxyEntity.get(0).getDescripiton();
        }
    }
}