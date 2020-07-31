package com.rjdiscbots.tftbot.discord.message;

import com.rjdiscbots.tftbot.exceptions.message.InvalidMessageException;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MessageReceivedEventHandler {

    private GalaxyMessageEventHandler galaxyMessageEventHandler;
    private ItemMessageEventHandler itemMessageEventHandler;
    private ListMessageEventHandler listMessageEventHandler;
    private BuildMessageEventHandler buildMessageEventHandler;
    private SynergyMessageEventHandler synergyMessageEventHandler;
    private ChampionMessageEventHandler championMessageEventHandler;

    @Autowired
    public MessageReceivedEventHandler(GalaxyMessageEventHandler galaxyMessageEventHandler,
        ItemMessageEventHandler itemMessageEventHandler,
        BuildMessageEventHandler buildMessageEventHandler,
        ListMessageEventHandler listMessageEventHandler,
        SynergyMessageEventHandler synergyMessageEventHandler,
        ChampionMessageEventHandler championMessageEventHandler) {
        this.galaxyMessageEventHandler = galaxyMessageEventHandler;
        this.itemMessageEventHandler = itemMessageEventHandler;
        this.buildMessageEventHandler = buildMessageEventHandler;
        this.listMessageEventHandler = listMessageEventHandler;
        this.synergyMessageEventHandler = synergyMessageEventHandler;
        this.championMessageEventHandler = championMessageEventHandler;
    }

    public void handleMessage(MessageReceivedEvent event) {
        String rawMessage = event.getMessage().getContentRaw();
        rawMessage = rawMessage.trim().toLowerCase();

        String returnMessage = null;

        try {
            if (rawMessage.startsWith("!galaxy ")) {
                returnMessage = galaxyMessageEventHandler.handleGalaxyMessage(rawMessage);
            } else if (rawMessage.startsWith("!list ")) {
                returnMessage = listMessageEventHandler.handleListMessage(rawMessage);
            } else if (rawMessage.startsWith("!item ")) {
                returnMessage = itemMessageEventHandler.handleItemMessage(rawMessage);
            } else if (rawMessage.startsWith("!build ")) {
                returnMessage = buildMessageEventHandler.handleBuildMessage(rawMessage);
            } else if (rawMessage.startsWith("!synergy ")) {
                returnMessage = synergyMessageEventHandler.handleSynergyMessage(rawMessage);
            } else if (rawMessage.startsWith("!champion ")) {
                returnMessage = championMessageEventHandler.handleChampionMessage(rawMessage);
            } else {
                return;
            }
        } catch (InvalidMessageException e) {
            returnMessage = e.getMessage();
        }

        event.getChannel().sendMessage(returnMessage).queue();
    }
}