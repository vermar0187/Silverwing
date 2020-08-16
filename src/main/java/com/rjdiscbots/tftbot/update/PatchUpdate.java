package com.rjdiscbots.tftbot.update;

import com.rjdiscbots.tftbot.config.DiscordConfig;
import com.rjdiscbots.tftbot.exceptions.parser.PatchProcessingException;
import java.io.IOException;
import java.util.List;
import javax.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PatchUpdate {

    private List<UpdateEntity> updateEntities;

    private DiscordConfig discordConfig;

    private final Logger logger = LoggerFactory.getLogger(PatchUpdate.class);

    @Autowired
    public PatchUpdate(List<UpdateEntity> updateEntities, DiscordConfig discordConfig) {
        this.updateEntities = updateEntities;
        this.discordConfig = discordConfig;
    }

    @PostConstruct
    public void onStartup() {
        if (discordConfig.isUpdate()) {
            updatePatch();
        }
    }

    public void updatePatch() {
        for (UpdateEntity updateEntity : updateEntities) {
            try {
                updateEntity.patch();
            } catch (PatchProcessingException | IOException e) {
                logger.warn(
                    String.format("Patch could not be parsed for %s.", updateEntity.getClass()), e);
            }
        }
    }
}
