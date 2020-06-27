package com.rjdiscbots.tftbot.discord.websocket;

import com.rjdiscbots.tftbot.config.DiscordConfig;
import javax.security.auth.login.LoginException;
import net.dv8tion.jda.api.AccountType;
import net.dv8tion.jda.api.JDABuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DiscordWebSocketBuilder {

    private JDABuilder jdaBuilder;

    public DiscordWebSocketBuilder() throws IllegalArgumentException {
        throw new IllegalArgumentException("Provide Discord Bot token");
    }

    @Autowired
    public DiscordWebSocketBuilder(DiscordConfig discordConfig, DiscordWebSocket discordWebSocket) throws LoginException {
        jdaBuilder = new JDABuilder(AccountType.BOT);
        jdaBuilder.setToken(discordConfig.getToken());
        jdaBuilder.addEventListeners(discordWebSocket);
        jdaBuilder.build();
    }
}