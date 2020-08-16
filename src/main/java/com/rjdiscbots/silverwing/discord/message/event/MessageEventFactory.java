package com.rjdiscbots.silverwing.discord.message.event;

import com.rjdiscbots.silverwing.exceptions.message.MessageEventDoesNotExistException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MessageEventFactory {

    private BuildMessageEventHandler buildMessageEventHandler;

    private ChampionMessageEventHandler championMessageEventHandler;

    private CompositionMessageEventHandler compositionMessageEventHandler;

    private GalaxyMessageEventHandler galaxyMessageEventHandler;

    private ItemMessageEventHandler itemMessageEventHandler;

    private ListMessageEventHandler listMessageEventHandler;

    private SynergyMessageEventHandler synergyMessageEventHandler;

    @Autowired
    public MessageEventFactory(BuildMessageEventHandler buildMessageEventHandler,
        ChampionMessageEventHandler championMessageEventHandler,
        CompositionMessageEventHandler compositionMessageEventHandler,
        GalaxyMessageEventHandler galaxyMessageEventHandler,
        ItemMessageEventHandler itemMessageEventHandler,
        ListMessageEventHandler listMessageEventHandler,
        SynergyMessageEventHandler synergyMessageEventHandler) {
        this.buildMessageEventHandler = buildMessageEventHandler;
        this.championMessageEventHandler = championMessageEventHandler;
        this.compositionMessageEventHandler = compositionMessageEventHandler;
        this.galaxyMessageEventHandler = galaxyMessageEventHandler;
        this.itemMessageEventHandler = itemMessageEventHandler;
        this.listMessageEventHandler = listMessageEventHandler;
        this.synergyMessageEventHandler = synergyMessageEventHandler;
    }

    public MessageEvent getMessageEventHandler(String rawMessage)
        throws MessageEventDoesNotExistException {
        if (rawMessage.startsWith("!galaxy")) {
            return galaxyMessageEventHandler;
        } else if (rawMessage.startsWith("!list")) {
            return listMessageEventHandler;
        } else if (rawMessage.startsWith("!item")) {
            return itemMessageEventHandler;
        } else if (rawMessage.startsWith("!build")) {
            return buildMessageEventHandler;
        } else if (rawMessage.startsWith("!synergy")) {
            return synergyMessageEventHandler;
        } else if (rawMessage.startsWith("!champion")) {
            return championMessageEventHandler;
        } else if (rawMessage.startsWith("!comp")) {
            return compositionMessageEventHandler;
        }

        throw new MessageEventDoesNotExistException();
    }
}
