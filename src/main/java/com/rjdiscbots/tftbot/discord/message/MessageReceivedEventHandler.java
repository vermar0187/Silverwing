package com.rjdiscbots.tftbot.discord.message;

import com.rjdiscbots.tftbot.exceptions.message.InvalidMessageException;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.requests.restaction.MessageAction;
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
    private CompositionMessageEventHandler compositionMessageEventHandler;

    @Autowired
    public MessageReceivedEventHandler(GalaxyMessageEventHandler galaxyMessageEventHandler,
        ItemMessageEventHandler itemMessageEventHandler,
        BuildMessageEventHandler buildMessageEventHandler,
        ListMessageEventHandler listMessageEventHandler,
        SynergyMessageEventHandler synergyMessageEventHandler,
        ChampionMessageEventHandler championMessageEventHandler,
        CompositionMessageEventHandler compositionMessageEventHandler) {
        this.galaxyMessageEventHandler = galaxyMessageEventHandler;
        this.itemMessageEventHandler = itemMessageEventHandler;
        this.buildMessageEventHandler = buildMessageEventHandler;
        this.listMessageEventHandler = listMessageEventHandler;
        this.synergyMessageEventHandler = synergyMessageEventHandler;
        this.championMessageEventHandler = championMessageEventHandler;
        this.compositionMessageEventHandler = compositionMessageEventHandler;
    }

    public void handleMessage(MessageReceivedEvent event) {
        String rawMessage = event.getMessage().getContentRaw();
        rawMessage = rawMessage.trim().toLowerCase();

        String errorMessage = null;

        EmbedBuilder embedBuilder = new EmbedBuilder();
        Map<String, String> filePathMap = new HashMap<>();
        embedBuilder.setAuthor("TFT Bot", null, "attachment://tft_icon.png");

        try {
            if (rawMessage.startsWith("!galaxy ")) {
                galaxyMessageEventHandler
                    .handleEmbedGalaxyMessage(rawMessage, embedBuilder, filePathMap);
            } else if (rawMessage.startsWith("!list ")) {
                listMessageEventHandler
                    .handleEmbedListMessage(rawMessage, embedBuilder, filePathMap);
            } else if (rawMessage.startsWith("!item ")) {
                itemMessageEventHandler
                    .handleEmbedItemMessage(rawMessage, embedBuilder, filePathMap);
            } else if (rawMessage.startsWith("!build ")) {
                buildMessageEventHandler
                    .handleEmbedBuildMessage(rawMessage, embedBuilder, filePathMap);
            } else if (rawMessage.startsWith("!synergy ")) {
                synergyMessageEventHandler
                    .handleEmbedSynergyMessage(rawMessage, embedBuilder, filePathMap);
            } else if (rawMessage.startsWith("!champion ")) {
                championMessageEventHandler
                    .handleEmbedChampionMessage(rawMessage, embedBuilder, filePathMap);
            } else if (rawMessage.startsWith("!comp ")) {
                compositionMessageEventHandler
                    .handleEmbedCompositionMessage(rawMessage, embedBuilder, filePathMap);
            } else {
                return;
            }
        } catch (InvalidMessageException e) {
            // need purposeful logging here
            errorMessage = e.getMessage();
        }

        MessageChannel messageChannel = event.getChannel();

        if (errorMessage != null) {
            sendErrorTextMessage(messageChannel, errorMessage);
        } else {
            sendEmbedMessage(messageChannel, embedBuilder, filePathMap);
        }
    }

    private void sendErrorTextMessage(MessageChannel messageChannel, String errorMessage) {
        messageChannel.sendMessage(errorMessage).queue();
    }

    private void sendEmbedMessage(MessageChannel messageChannel, EmbedBuilder embedBuilder,
        Map<String, String> filePathMap) {
        File tftIconFile = new File("assets/tft_icon.png");
        MessageAction messageAction = messageChannel.sendFile(tftIconFile, "tft_icon.png");

        for (Map.Entry<String, String> fileName : filePathMap.entrySet()) {
            File imgFile = new File(fileName.getValue());
            try {
                messageAction = messageAction.addFile(imgFile, fileName.getKey());
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }

        messageAction.embed(embedBuilder.build()).queue();
    }
}