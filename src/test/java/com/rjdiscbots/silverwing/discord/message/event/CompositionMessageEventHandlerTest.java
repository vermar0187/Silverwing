package com.rjdiscbots.silverwing.discord.message.event;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;

import com.rjdiscbots.silverwing.TestHelper;
import com.rjdiscbots.silverwing.db.compositions.CompositionItemsRepository;
import com.rjdiscbots.silverwing.db.compositions.CompositionRepository;
import com.rjdiscbots.silverwing.db.items.ItemsRepository;
import com.rjdiscbots.silverwing.exceptions.message.EntityDoesNotExistException;
import com.rjdiscbots.silverwing.exceptions.message.NoArgumentProvidedException;
import java.util.HashMap;
import java.util.Map;
import net.dv8tion.jda.api.EmbedBuilder;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.AfterEach;

public class CompositionMessageEventHandlerTest {

    private CompositionRepository compositionRepository;

    private CompositionItemsRepository compositionItemsRepository;

    private ItemsRepository itemsRepository;

    private CompositionMessageEventHandler compositionMessageEventHandler;

    @Before
    public void setup() {
        compositionRepository = mock(CompositionRepository.class);
        compositionItemsRepository = mock(CompositionItemsRepository.class);
        itemsRepository = mock(ItemsRepository.class);

        compositionMessageEventHandler = new CompositionMessageEventHandler(compositionRepository,
            compositionItemsRepository, itemsRepository);
    }

    @AfterEach
    public void tearDown() {
        reset(compositionRepository);
        reset(compositionItemsRepository);
        reset(itemsRepository);
    }

    @Test
    public void whenGivenInvalidCompCommand_then_throwIllegalArgumentException() {
        String invalidCompCommand = "!invalid comp command";
        EmbedBuilder embedBuilder = new EmbedBuilder();
        Map<String, String> filePathMap = new HashMap<>();

        IllegalArgumentException illegalArgumentException = assertThrows(
            IllegalArgumentException.class, () ->
                compositionMessageEventHandler
                    .handleEmbedMessage(invalidCompCommand, embedBuilder, filePathMap));

        assertEquals(TestHelper.invalidCommandErrorMessage("comp", invalidCompCommand),
            illegalArgumentException.getMessage());
    }

    @Test
    public void whenGivenValidCompCommandWithNoArg_then_throwNoArgumentProvidedException() {
        String compCommandWithNoArg = "!comp";
        EmbedBuilder embedBuilder = new EmbedBuilder();
        Map<String, String> filePathMap = new HashMap<>();

        NoArgumentProvidedException noArgumentProvidedException = assertThrows(
            NoArgumentProvidedException.class, () ->
                compositionMessageEventHandler
                    .handleEmbedMessage(compCommandWithNoArg, embedBuilder,
                        filePathMap));

        assertEquals(TestHelper.validCommandWithNoArgumentsErrorMessage("composition"),
            noArgumentProvidedException.getMessage());
    }

    @Test
    public void whenCompCannotBeFound_then_throwEntityDoesNotExistException() {
        String compCommand = "!comp " + TestHelper.COMP_NAME_ONE;
        EmbedBuilder embedBuilder = new EmbedBuilder();
        Map<String, String> filePathMap = new HashMap<>();

        when(compositionRepository.findOneByName(TestHelper.COMP_NAME_ONE))
            .thenReturn(null);

        EntityDoesNotExistException entityDoesNotExistException = assertThrows(
            EntityDoesNotExistException.class, () ->
                compositionMessageEventHandler
                    .handleEmbedMessage(compCommand, embedBuilder,
                        filePathMap));

        assertEquals(TestHelper.entityNotFoundErrorMessage("composition"),
            entityDoesNotExistException.getMessage());
    }
}
