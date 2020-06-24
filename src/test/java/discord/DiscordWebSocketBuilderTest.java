package discord;

import javax.security.auth.login.LoginException;
import org.junit.Test;

public class DiscordWebSocketBuilderTest {

    @Test(expected = IllegalArgumentException.class)
    public void whenNoToken_then_throwIllegalArgumentException() throws IllegalArgumentException {
        new DiscordWebSocketBuilder();
    }

    @Test(expected = LoginException.class)
    public void whenInvalidToken_then_throwLoginException() throws LoginException {
        new DiscordWebSocketBuilder("");
    }
}