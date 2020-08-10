package com.rjdiscbots.tftbot.discord.message.service;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public interface MessageService {

    void handleMessage(MessageReceivedEvent messageReceivedEvent);
}
