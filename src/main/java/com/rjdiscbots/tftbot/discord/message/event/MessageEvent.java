package com.rjdiscbots.tftbot.discord.message.event;

import com.rjdiscbots.tftbot.exceptions.message.InvalidMessageException;
import java.util.Map;
import net.dv8tion.jda.api.EmbedBuilder;
import org.springframework.lang.NonNull;

public interface MessageEvent {

    void handleEmbedMessage(@NonNull String rawMessage, @NonNull EmbedBuilder embedBuilder,
        @NonNull Map<String, String> filePathMap) throws InvalidMessageException;
}
