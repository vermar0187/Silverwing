package com.rjdiscbots.tftbot.discord.message;

import com.rjdiscbots.tftbot.db.galaxies.GalaxiesEntity;
import com.rjdiscbots.tftbot.db.galaxies.GalaxiesRepository;
import java.util.List;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MessageReceivedEventHandler {

    private GalaxiesRepository galaxiesRepository;

    @Autowired
    public MessageReceivedEventHandler(GalaxiesRepository galaxiesRepository) {
        this.galaxiesRepository = galaxiesRepository;
    }

    public void handleMessage(MessageReceivedEvent event) {
        String rawMessage = event.getMessage().getContentRaw();
        String returnMessage = null;

        if (rawMessage.startsWith("!galaxy ")) {
            returnMessage = handleGalaxyMessage(rawMessage);
        } else {
            return;
        }

        event.getChannel().sendMessage(returnMessage).queue();
    }

    private String handleGalaxyMessage(String rawGalaxyMessage) {
        rawGalaxyMessage = rawGalaxyMessage.replaceFirst("!galaxy ", "");
        rawGalaxyMessage = rawGalaxyMessage.replaceAll("\"", "");

        List<GalaxiesEntity> galaxiesEntity = galaxiesRepository.findByName(rawGalaxyMessage);

        if (galaxiesEntity.isEmpty()) {
            return "No such galaxy exists!";
        } else {
            return galaxiesEntity.get(0).getDescripiton();
        }
    }

}
