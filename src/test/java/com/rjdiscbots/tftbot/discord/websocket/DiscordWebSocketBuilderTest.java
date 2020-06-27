package com.rjdiscbots.tftbot.discord.websocket;

import static org.mockito.Mockito.mock;

import com.rjdiscbots.tftbot.config.DiscordConfig;
import javax.security.auth.login.LoginException;
import org.junit.Test;

public class DiscordWebSocketBuilderTest {

    @Test(expected = IllegalArgumentException.class)
    public void whenNoToken_then_throwIllegalArgumentException() throws IllegalArgumentException {
        new DiscordWebSocketBuilder();
    }

    @Test(expected = LoginException.class)
    public void whenInvalidToken_then_throwLoginException() throws LoginException {
        DiscordConfig discordConfig = new DiscordConfig();
        discordConfig.setToken("dead-beef");
        DiscordWebSocket discordWebSocket = mock(DiscordWebSocket.class);

        new DiscordWebSocketBuilder(discordConfig, discordWebSocket);
    }
}