package com.rjdiscbots.tftbot.discord.message;

import com.rjdiscbots.tftbot.db.galaxies.GalaxiesRepository;
import com.rjdiscbots.tftbot.db.galaxies.GalaxyEntity;
import com.rjdiscbots.tftbot.exceptions.message.EntityDoesNotExistException;
import com.rjdiscbots.tftbot.exceptions.message.InvalidMessageException;
import com.rjdiscbots.tftbot.utility.DiscordMessageHelper;
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

    public String handleGalaxyMessage(String rawGalaxyMessage) throws InvalidMessageException {
        if (!rawGalaxyMessage.startsWith("!galaxy ")) {
            throw new IllegalArgumentException(
                "Message does begin with !galaxy: " + rawGalaxyMessage);
        }

        rawGalaxyMessage = rawGalaxyMessage.replaceFirst("!galaxy ", "");
        rawGalaxyMessage = rawGalaxyMessage.trim();

        return fetchGalaxyDescription(rawGalaxyMessage);
    }

    private String fetchGalaxyDescription(String galaxyName) throws EntityDoesNotExistException {
        List<GalaxyEntity> galaxyEntity = galaxiesRepository.findByName(galaxyName);

        if (galaxyEntity == null || galaxyEntity.isEmpty()) {
            throw new EntityDoesNotExistException("Invalid galaxy provided!");
        }

        StringBuilder returnMessage = new StringBuilder();

        GalaxyEntity galaxy = galaxyEntity.get(0);

        String formattedGalaxyName = DiscordMessageHelper.formatName(galaxy.getName());
        returnMessage.append("__").append(formattedGalaxyName).append("__").append("\n");
        returnMessage.append(galaxy.getDescripiton());

        return returnMessage.toString();
    }
}