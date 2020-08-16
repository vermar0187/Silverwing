package com.rjdiscbots.silverwing.discord.message.event;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;

import com.rjdiscbots.silverwing.TestHelper;
import com.rjdiscbots.silverwing.db.champions.ChampionsRepository;
import com.rjdiscbots.silverwing.db.synergies.SynergyRepository;
import com.rjdiscbots.silverwing.exceptions.message.EntityDoesNotExistException;
import com.rjdiscbots.silverwing.exceptions.message.NoArgumentProvidedException;
import java.util.HashMap;
import java.util.Map;
import net.dv8tion.jda.api.EmbedBuilder;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.AfterEach;

public class SynergyMessageEventHandlerTest {

    private SynergyRepository synergyRepository;

    private ChampionsRepository championsRepository;

    private SynergyMessageEventHandler synergyMessageEventHandler;

    @Before
    public void setup() {
        synergyRepository = mock(SynergyRepository.class);
        championsRepository = mock(ChampionsRepository.class);
        synergyMessageEventHandler = new SynergyMessageEventHandler(synergyRepository,
            championsRepository);
    }

    @AfterEach
    public void tearDown() {
        reset(synergyRepository);
        reset(championsRepository);
    }

    @Test
    public void whenGivenInvalidSynergyCommand_then_throwIllegalArgumentException() {
        String invalidSynergyCommand = "!invalid synergy command";
        EmbedBuilder embedBuilder = new EmbedBuilder();
        Map<String, String> filePathMap = new HashMap<>();

        IllegalArgumentException illegalArgumentException = assertThrows(
            IllegalArgumentException.class, () ->
                synergyMessageEventHandler
                    .handleEmbedMessage(invalidSynergyCommand, embedBuilder, filePathMap));

        assertEquals(TestHelper.invalidCommandErrorMessage("synergy", invalidSynergyCommand),
            illegalArgumentException.getMessage());
    }

    @Test
    public void whenGivenValidSynergyCommandWithNoArg_then_throwNoArgumentProvidedException() {
        String synergyCommandWithNoArg = "!synergy";
        EmbedBuilder embedBuilder = new EmbedBuilder();
        Map<String, String> filePathMap = new HashMap<>();

        NoArgumentProvidedException noArgumentProvidedException = assertThrows(
            NoArgumentProvidedException.class, () ->
                synergyMessageEventHandler
                    .handleEmbedMessage(synergyCommandWithNoArg, embedBuilder,
                        filePathMap));

        assertEquals(TestHelper.validCommandWithNoArgumentsErrorMessage("synergy"),
            noArgumentProvidedException.getMessage());
    }

    @Test
    public void whenSynergyCannotBeFound_then_throwEntityDoesNotExistException() {
        String synergyCommand = "!synergy " + TestHelper.SYNERGY_NAME_ONE;
        EmbedBuilder embedBuilder = new EmbedBuilder();
        Map<String, String> filePathMap = new HashMap<>();

        when(synergyRepository.findOneByName(TestHelper.SYNERGY_NAME_ONE))
            .thenReturn(null);

        EntityDoesNotExistException entityDoesNotExistException = assertThrows(
            EntityDoesNotExistException.class, () ->
                synergyMessageEventHandler
                    .handleEmbedMessage(synergyCommand, embedBuilder,
                        filePathMap));

        assertEquals(TestHelper.entityNotFoundErrorMessage("synergy"),
            entityDoesNotExistException.getMessage());
    }
}
