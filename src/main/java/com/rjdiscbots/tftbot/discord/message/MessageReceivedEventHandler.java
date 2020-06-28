package com.rjdiscbots.tftbot.discord.message;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MessageReceivedEventHandler {

    private GalaxyMessageEventHandler galaxyMessageEventHandler;
    private ItemMessageEventHandler itemMessageEventHandler;

    @Autowired
    public MessageReceivedEventHandler(GalaxyMessageEventHandler galaxyMessageEventHandler,
        ItemMessageEventHandler itemMessageEventHandler) {
        this.galaxyMessageEventHandler = galaxyMessageEventHandler;
        this.itemMessageEventHandler = itemMessageEventHandler;
    }

    public void handleMessage(MessageReceivedEvent event) {
        String rawMessage = event.getMessage().getContentRaw();
        String returnMessage = null;

        if (rawMessage.startsWith("!galaxy ")) {
            returnMessage = galaxyMessageEventHandler.handleGalaxyMessage(rawMessage);
        } else if (rawMessage.startsWith("!list galaxy") || rawMessage
            .startsWith("!list galaxies")) {
            returnMessage = galaxyMessageEventHandler.handleListGalaxyMessage();
        } else if (rawMessage.startsWith("!item ")) {
            returnMessage = itemMessageEventHandler.handleItemMessage(rawMessage);
        } else if (rawMessage.startsWith("!list components")) {
            returnMessage = itemMessageEventHandler.handleComponentsMessage(rawMessage);
        } else {
            return;
        }

        event.getChannel().sendMessage(returnMessage).queue();
    }
}