package com.rjdiscbots.silverwing.discord.message.event;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;

import com.rjdiscbots.silverwing.TestHelper;
import com.rjdiscbots.silverwing.db.compositions.CompositionRepository;
import com.rjdiscbots.silverwing.db.galaxies.GalaxiesRepository;
import com.rjdiscbots.silverwing.db.items.ItemsRepository;
import com.rjdiscbots.silverwing.db.synergies.SynergyRepository;
import com.rjdiscbots.silverwing.exceptions.message.NoArgumentProvidedException;
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
