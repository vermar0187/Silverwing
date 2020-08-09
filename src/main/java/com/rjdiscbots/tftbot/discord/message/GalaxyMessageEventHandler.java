package com.rjdiscbots.tftbot.discord.message;

import com.rjdiscbots.tftbot.db.galaxies.GalaxiesRepository;
import com.rjdiscbots.tftbot.db.galaxies.GalaxyEntity;
import com.rjdiscbots.tftbot.exceptions.message.EntityDoesNotExistException;
import com.rjdiscbots.tftbot.exceptions.message.InvalidMessageException;
import com.rjdiscbots.tftbot.utility.DiscordMessageHelper;
import java.util.Map;
import net.dv8tion.jda.api.EmbedBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

@Component
public class GalaxyMessageEventHandler {

    private GalaxiesRepository galaxiesRepository;

    @Autowired
    public GalaxyMessageEventHandler(GalaxiesRepository galaxiesRepository) {
        this.galaxiesRepository = galaxiesRepository;
    }

    public void handleEmbedGalaxyMessage(@NonNull String rawGalaxyMessage,
        @NonNull EmbedBuilder embedBuilder, @NonNull Map<String, String> filePathMap)
        throws InvalidMessageException {
        if (!rawGalaxyMessage.startsWith("!galaxy ")) {
            throw new IllegalArgumentException(
                "Message does begin with !galaxy: " + rawGalaxyMessage);
        }

        rawGalaxyMessage = rawGalaxyMessage.replaceFirst("!galaxy ", "");
        rawGalaxyMessage = rawGalaxyMessage.trim();

        fetchGalaxyDescription(rawGalaxyMessage, embedBuilder, filePathMap);
    }

    private void fetchGalaxyDescription(String galaxyName, EmbedBuilder embedBuilder,
        Map<String, String> filePathMap) throws EntityDoesNotExistException {
        GalaxyEntity galaxy = galaxiesRepository.findOneByName(galaxyName);

        if (galaxy == null) {
            throw new EntityDoesNotExistException("Invalid galaxy provided!");
        }

        String formattedGalaxyName = DiscordMessageHelper.formatName(galaxy.getName());

        String picUrl = galaxy.getKey() + ".png";
        filePathMap.put(picUrl, "patch/galaxies/" + picUrl);

        embedBuilder.setTitle(formattedGalaxyName);
        embedBuilder.setDescription(galaxy.getDescripiton());
        embedBuilder.setThumbnail("attachment://" + picUrl);
    }
}