package discord;

import javax.annotation.Nonnull;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DiscordWebSocket extends ListenerAdapter {

    private final Logger logger = LoggerFactory.getLogger(DiscordWebSocket.class);

    public DiscordWebSocket() {

    }

    @Override
    public void onMessageReceived(@Nonnull MessageReceivedEvent event) {
        logger.info("We received a message from " +
            event.getAuthor().getName() + ": " +
            event.getMessage().getContentDisplay()
        );

        if (event.getMessage().getContentRaw().equals("!ping")) {
            event.getChannel().sendMessage("Pong!").queue();
        }
    }

}