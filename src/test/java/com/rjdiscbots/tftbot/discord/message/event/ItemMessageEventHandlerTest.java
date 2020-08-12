package com.rjdiscbots.tftbot.discord.message.event;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;

import com.rjdiscbots.tftbot.TestHelper;
import com.rjdiscbots.tftbot.db.items.ItemsRepository;
import com.rjdiscbots.tftbot.exceptions.message.EntityDoesNotExistException;
import com.rjdiscbots.tftbot.exceptions.message.NoArgumentProvidedException;
import java.util.HashMap;
import java.util.Map;
import net.dv8tion.jda.api.EmbedBuilder;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.AfterEach;

public class ItemMessageEventHandlerTest {

    private ItemsRepository itemsRepository;

    private ItemMessageEventHandler itemMessageEventHandler;

    @Before
    public void setup() {
        itemsRepository = mock(ItemsRepository.class);
        itemMessageEventHandler = new ItemMessageEventHandler(itemsRepository);
    }

    @AfterEach
    public void tearDown() {
        reset(itemsRepository);
    }

    @Test
    public void whenGivenInvalidItemCommand_then_throwIllegalArgumentException() {
        String invalidItemCommand = "!invalid item command";
        EmbedBuilder embedBuilder = new EmbedBuilder();
        Map<String, String> filePathMap = new HashMap<>();

        IllegalArgumentException illegalArgumentException = assertThrows(
            IllegalArgumentException.class, () ->
                itemMessageEventHandler
                    .handleEmbedMessage(invalidItemCommand, embedBuilder, filePathMap));

        assertEquals(TestHelper.invalidCommandErrorMessage("item", invalidItemCommand),
            illegalArgumentException.getMessage());
    }

    @Test
    public void whenGivenValidItemCommandWithNoArg_then_throwNoArgumentProvidedException() {
        String itemCommandWithNoArg = "!item";
        EmbedBuilder embedBuilder = new EmbedBuilder();
        Map<String, String> filePathMap = new HashMap<>();

        NoArgumentProvidedException noArgumentProvidedException = assertThrows(
            NoArgumentProvidedException.class, () ->
                itemMessageEventHandler
                    .handleEmbedMessage(itemCommandWithNoArg, embedBuilder,
                        filePathMap));

        assertEquals(TestHelper.validCommandWithNoArgumentsErrorMessage("item"),
            noArgumentProvidedException.getMessage());
    }

    @Test
    public void whenItemCannotBeFound_then_throwEntityDoesNotExistException() {
        String itemCommand = "!item " + TestHelper.FULL_ITEM_NAME_ONE;
        EmbedBuilder embedBuilder = new EmbedBuilder();
        Map<String, String> filePathMap = new HashMap<>();

        when(itemsRepository.findOneByName(TestHelper.FULL_ITEM_NAME_ONE))
            .thenReturn(null);

        EntityDoesNotExistException entityDoesNotExistException = assertThrows(
            EntityDoesNotExistException.class, () ->
                itemMessageEventHandler
                    .handleEmbedMessage(itemCommand, embedBuilder,
                        filePathMap));

        assertEquals(TestHelper.entityNotFoundErrorMessage("item"),
            entityDoesNotExistException.getMessage());
    }
}
