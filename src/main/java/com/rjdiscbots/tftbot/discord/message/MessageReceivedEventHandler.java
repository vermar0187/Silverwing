package com.rjdiscbots.tftbot.discord.message;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MessageReceivedEventHandler {

    private GalaxyMessageEventHandler galaxyMessageEventHandler;

    @Autowired
    public MessageReceivedEventHandler(GalaxyMessageEventHandler galaxyMessageEventHandler) {
        this.galaxyMessageEventHandler = galaxyMessageEventHandler;
    }

    public void handleMessage(MessageReceivedEvent event) {
        String rawMessage = event.getMessage().getContentRaw();
        String returnMessage = null;

        if (rawMessage.startsWith("!galaxy ")) {
            returnMessage = galaxyMessageEventHandler.handleGalaxyMessage(rawMessage);
        } else if (rawMessage.startsWith("!list galaxy") || rawMessage.startsWith("!list galaxies")) {
            returnMessage = galaxyMessageEventHandler.handleListGalaxyMessage();
        } else {
            return;
        }

        event.getChannel().sendMessage(returnMessage).queue();
    }
}
