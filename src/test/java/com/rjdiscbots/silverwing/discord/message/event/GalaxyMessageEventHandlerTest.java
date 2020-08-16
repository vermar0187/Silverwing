package com.rjdiscbots.silverwing.discord.message.event;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;

import com.rjdiscbots.silverwing.TestHelper;
import com.rjdiscbots.silverwing.db.galaxies.GalaxiesRepository;
import com.rjdiscbots.silverwing.exceptions.message.EntityDoesNotExistException;
import com.rjdiscbots.silverwing.exceptions.message.NoArgumentProvidedException;
import java.util.HashMap;
import java.util.Map;
import net.dv8tion.jda.api.EmbedBuilder;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.AfterEach;

public class GalaxyMessageEventHandlerTest {

    private GalaxiesRepository galaxiesRepository;

    private GalaxyMessageEventHandler galaxyMessageEventHandler;

    @Before
    public void setup() {
        galaxiesRepository = mock(GalaxiesRepository.class);
        galaxyMessageEventHandler = new GalaxyMessageEventHandler(galaxiesRepository);
    }

    @AfterEach
    public void tearDown() {
        reset(galaxiesRepository);
    }

    @Test
    public void whenGivenInvalidGalaxyCommand_then_throwIllegalArgumentException() {
        String invalidGalaxyCommand = "!invalid galaxy command";
        EmbedBuilder embedBuilder = new EmbedBuilder();
        Map<String, String> filePathMap = new HashMap<>();

        IllegalArgumentException illegalArgumentException = assertThrows(
            IllegalArgumentException.class, () ->
                galaxyMessageEventHandler
                    .handleEmbedMessage(invalidGalaxyCommand, embedBuilder, filePathMap));

        assertEquals(TestHelper.invalidCommandErrorMessage("galaxy", invalidGalaxyCommand),
            illegalArgumentException.getMessage());
    }

    @Test
    public void whenGivenValidGalaxyCommandWithNoArg_then_throwNoArgumentProvidedException() {
        String galaxyCommandWithNoArg = "!galaxy";
        EmbedBuilder embedBuilder = new EmbedBuilder();
        Map<String, String> filePathMap = new HashMap<>();

        NoArgumentProvidedException noArgumentProvidedException = assertThrows(
            NoArgumentProvidedException.class, () ->
                galaxyMessageEventHandler
                    .handleEmbedMessage(galaxyCommandWithNoArg, embedBuilder,
                        filePathMap));

        assertEquals(TestHelper.validCommandWithNoArgumentsErrorMessage("galaxy"),
            noArgumentProvidedException.getMessage());
    }

    @Test
    public void whenGalaxyCannotBeFound_then_throwEntityDoesNotExistException() {
        String galaxyCommand = "!galaxy " + TestHelper.GALAXY_NAME_ONE;
        EmbedBuilder embedBuilder = new EmbedBuilder();
        Map<String, String> filePathMap = new HashMap<>();

        when(galaxiesRepository.findOneByName(TestHelper.GALAXY_NAME_ONE))
            .thenReturn(null);

        EntityDoesNotExistException entityDoesNotExistException = assertThrows(
            EntityDoesNotExistException.class, () ->
                galaxyMessageEventHandler
                    .handleEmbedMessage(galaxyCommand, embedBuilder,
                        filePathMap));

        assertEquals(TestHelper.entityNotFoundErrorMessage("galaxy"),
            entityDoesNotExistException.getMessage());
    }
}
