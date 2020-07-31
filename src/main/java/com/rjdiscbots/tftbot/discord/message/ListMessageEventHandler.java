package com.rjdiscbots.tftbot.discord.message;

import com.rjdiscbots.tftbot.db.galaxies.GalaxiesRepository;
import com.rjdiscbots.tftbot.db.galaxies.GalaxyEntity;
import com.rjdiscbots.tftbot.db.items.ItemEntity;
import com.rjdiscbots.tftbot.db.items.ItemsRepository;
import com.rjdiscbots.tftbot.db.synergies.SynergyEntity;
import com.rjdiscbots.tftbot.db.synergies.SynergyRepository;
import com.rjdiscbots.tftbot.exceptions.message.CommandDoesNotExistException;
import com.rjdiscbots.tftbot.exceptions.message.EntityDoesNotExistException;
import com.rjdiscbots.tftbot.exceptions.message.InvalidMessageException;
import com.rjdiscbots.tftbot.utility.DiscordMessageHelper;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ListMessageEventHandler {

    private ItemsRepository itemsRepository;

    private GalaxiesRepository galaxiesRepository;

    private SynergyRepository synergyRepository;

    @Autowired
    public ListMessageEventHandler(ItemsRepository itemsRepository,
        GalaxiesRepository galaxiesRepository,
        SynergyRepository synergyRepository) {
        this.itemsRepository = itemsRepository;
        this.galaxiesRepository = galaxiesRepository;
        this.synergyRepository = synergyRepository;
    }

    public String handleListMessage(String rawListMessage) throws InvalidMessageException {
        if (!rawListMessage.startsWith("!list ")) {
            throw new IllegalArgumentException(
                "Message does begin with !list: " + rawListMessage);
        }

        rawListMessage = rawListMessage.replaceFirst("!list ", "");
        rawListMessage = rawListMessage.trim();

        if (rawListMessage.startsWith("galaxies")) {
            return fetchAllGalaxies();
        } else if (rawListMessage.startsWith("components")) {
            return fetchAllComponents();
        } else if (rawListMessage.startsWith("commands")) {
            return fetchAllCommands();
        } else if (rawListMessage.startsWith("synergies")) {
            return fetchAllSynergies();
        } else {
            throw new CommandDoesNotExistException("Invalid command provided!");
        }
    }

    private String fetchAllSynergies() throws EntityDoesNotExistException {
        List<SynergyEntity> synergyEntities = synergyRepository.findAll();

        if (synergyEntities.isEmpty()) {
            throw new EntityDoesNotExistException("No synergies could be found!");
        }

        StringBuilder returnMessage = new StringBuilder();
        returnMessage.append("__").append("Synergies").append("__");

        for (SynergyEntity synergyEntity : synergyEntities) {
            String formattedSynergyName = DiscordMessageHelper
                .formatName(synergyEntity.getName());
            returnMessage.append("\n").append(formattedSynergyName);
        }

        return returnMessage.toString();
    }

    private String fetchAllCommands() {
        return "__" + "Commands" + "__" + "\n"
            + "!champion <champion name>" + "\n"
            + "!list [galaxies, components, commands, synergies]" + "\n"
            + "!item <item name>" + "\n"
            + "!galaxy <galaxy name>" + "\n"
            + "!synergy <synergy name>" + "\n"
            + "!build [--desc] <item component 1>, <item component 2>, ..."
            + "\n";
    }

    private String fetchAllGalaxies() throws EntityDoesNotExistException {
        List<GalaxyEntity> galaxiesEntities = galaxiesRepository.findAll();

        if (galaxiesEntities.isEmpty()) {
            throw new EntityDoesNotExistException("No galaxies could be found!");
        }

        StringBuilder returnMessage = new StringBuilder();
        returnMessage.append("__").append("Galaxies").append("__");

        for (GalaxyEntity galaxiesEntity : galaxiesEntities) {
            String formattedGalaxyName = DiscordMessageHelper.formatName(galaxiesEntity.getName());
            returnMessage.append("\n").append(formattedGalaxyName);
        }

        return returnMessage.toString();
    }

    private String fetchAllComponents() throws EntityDoesNotExistException {
        List<ItemEntity> itemEntities = itemsRepository
            .findByComponentOneIsNullAndComponentTwoIsNull();

        if (itemEntities == null || itemEntities.isEmpty()) {
            throw new EntityDoesNotExistException("No components could be found!");
        }

        StringBuilder returnMessage = new StringBuilder();
        returnMessage.append("__").append("Components").append("__");

        for (ItemEntity itemEntity : itemEntities) {
            String formattedItemName = DiscordMessageHelper.formatName(itemEntity.getName());

            returnMessage.append("\n").append(formattedItemName).append(": ")
                .append(itemEntity.getDescription());
        }

        return returnMessage.toString();
    }
}
