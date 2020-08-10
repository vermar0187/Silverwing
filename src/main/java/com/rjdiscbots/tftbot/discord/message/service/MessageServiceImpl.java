package com.rjdiscbots.tftbot.discord.message.service;

import com.rjdiscbots.tftbot.discord.message.event.MessageEvent;
import com.rjdiscbots.tftbot.discord.message.event.MessageEventFactory;
import com.rjdiscbots.tftbot.exceptions.message.InvalidMessageException;
import com.rjdiscbots.tftbot.exceptions.message.MessageEventDoesNotExistException;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.requests.restaction.MessageAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MessageServiceImpl implements MessageService {

    private MessageEventFactory messageEventFactory;

    private final Logger logger = LoggerFactory.getLogger(MessageServiceImpl.class);

    @Autowired
    public MessageServiceImpl(MessageEventFactory messageEventFactory) {
        this.messageEventFactory = messageEventFactory;
    }

    @Override
    public void handleMessage(MessageReceivedEvent event) {
        String rawMessage = event.getMessage().getContentRaw();
        rawMessage = rawMessage.trim().toLowerCase();

        String errorMessage = null;

        EmbedBuilder embedBuilder = new EmbedBuilder();
        Map<String, String> filePathMap = new HashMap<>();
        embedBuilder.setAuthor("TFT Bot", null, "attachment://tft_icon.png");

        try {
            MessageEvent messageEventHandler = messageEventFactory
                .getMessageEventHandler(rawMessage);
            messageEventHandler.handleEmbedMessage(rawMessage, embedBuilder, filePathMap);
        } catch (InvalidMessageException e) {
            // need purposeful logging here
            errorMessage = e.getMessage();
            logger.info(String.format("Command %s could not be handled", rawMessage),
                e.fillInStackTrace());
        } catch (MessageEventDoesNotExistException e) {
            return;
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
                logger.warn(String.format("File could not be processed: %s", fileName.getValue()),
                    e.fillInStackTrace());
            }
        }

        messageAction.embed(embedBuilder.build()).queue();
    }
}