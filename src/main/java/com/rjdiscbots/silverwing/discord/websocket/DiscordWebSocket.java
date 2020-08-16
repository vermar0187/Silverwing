package com.rjdiscbots.silverwing.discord.websocket;

import com.rjdiscbots.silverwing.discord.message.service.MessageService;
import javax.annotation.Nonnull;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DiscordWebSocket extends ListenerAdapter {

    private MessageService messageService;

    @Autowired
    public DiscordWebSocket(MessageService messageService) {
        this.messageService = messageService;
    }

    @Override
    public void onMessageReceived(@Nonnull MessageReceivedEvent event) {
        messageService.handleMessage(event);
    }
}