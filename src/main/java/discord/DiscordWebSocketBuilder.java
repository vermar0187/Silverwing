package discord;

import javax.security.auth.login.LoginException;
import net.dv8tion.jda.api.AccountType;
import net.dv8tion.jda.api.JDABuilder;

public class DiscordWebSocketBuilder {

    private JDABuilder jdaBuilder;

    public DiscordWebSocketBuilder() throws IllegalArgumentException {
        throw new IllegalArgumentException("Provide Discord Bot token");
    }

    public DiscordWebSocketBuilder(String token) throws LoginException {
        jdaBuilder = new JDABuilder(AccountType.BOT);
        jdaBuilder.setToken(token);
        jdaBuilder.addEventListeners(new DiscordWebSocket());
        jdaBuilder.build();
    }
}