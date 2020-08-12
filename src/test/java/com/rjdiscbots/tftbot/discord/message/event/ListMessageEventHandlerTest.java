package com.rjdiscbots.tftbot.discord.message.event;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;

import com.rjdiscbots.tftbot.TestHelper;
import com.rjdiscbots.tftbot.db.compositions.CompositionRepository;
import com.rjdiscbots.tftbot.db.galaxies.GalaxiesRepository;
import com.rjdiscbots.tftbot.db.items.ItemsRepository;
import com.rjdiscbots.tftbot.db.synergies.SynergyRepository;
import com.rjdiscbots.tftbot.exceptions.message.NoArgumentProvidedException;
import java.util.HashMap;
import java.util.Map;
import net.dv8tion.jda.api.EmbedBuilder;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.AfterEach;

public class ListMessageEventHandlerTest {

    private ItemsRepository itemsRepository;

    private GalaxiesRepository galaxiesRepository;

    private CompositionRepository compositionRepository;

    private SynergyRepository synergyRepository;

    private ListMessageEventHandler listMessageEventHandler;

    @Before
    public void setup() {
        itemsRepository = mock(ItemsRepository.class);
        galaxiesRepository = mock(GalaxiesRepository.class);
        compositionRepository = mock(CompositionRepository.class);
        synergyRepository = mock(SynergyRepository.class);
        listMessageEventHandler = new ListMessageEventHandler(itemsRepository, galaxiesRepository,
            synergyRepository, compositionRepository);
    }

    @AfterEach
    public void tearDown() {
        reset(itemsRepository);
        reset(galaxiesRepository);
        reset(compositionRepository);
        reset(synergyRepository);
    }

    @Test
    public void whenGivenInvalidListCommand_then_throwIllegalArgumentException() {
        String invalidListCommand = "!invalid list command";
        EmbedBuilder embedBuilder = new EmbedBuilder();
        Map<String, String> filePathMap = new HashMap<>();

        IllegalArgumentException illegalArgumentException = assertThrows(
            IllegalArgumentException.class, () ->
                listMessageEventHandler
                    .handleEmbedMessage(invalidListCommand, embedBuilder, filePathMap));

        assertEquals(TestHelper.invalidCommandErrorMessage("list", invalidListCommand),
            illegalArgumentException.getMessage());
    }

    @Test
    public void whenGivenValidListCommandWithNoArg_then_throwNoArgumentProvidedException() {
        String synergyCommandWithNoArg = "!list";
        EmbedBuilder embedBuilder = new EmbedBuilder();
        Map<String, String> filePathMap = new HashMap<>();

        NoArgumentProvidedException noArgumentProvidedException = assertThrows(
            NoArgumentProvidedException.class, () ->
                listMessageEventHandler
                    .handleEmbedMessage(synergyCommandWithNoArg, embedBuilder,
                        filePathMap));

        assertEquals(TestHelper.validCommandWithNoArgumentsErrorMessage("list command"),
            noArgumentProvidedException.getMessage());
    }
}
