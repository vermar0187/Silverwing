package com.rjdiscbots.tftbot.discord.message.event;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;

import com.rjdiscbots.tftbot.TestHelper;
import com.rjdiscbots.tftbot.db.items.ItemEntity;
import com.rjdiscbots.tftbot.db.items.ItemsRepository;
import com.rjdiscbots.tftbot.exceptions.message.EntityDoesNotExistException;
import com.rjdiscbots.tftbot.exceptions.message.InvalidMessageException;
import com.rjdiscbots.tftbot.exceptions.message.NoArgumentProvidedException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed.Field;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.AfterEach;

public class BuildMessageEventHandlerTest {

    private ItemsRepository itemsRepository;

    private BuildMessageEventHandler buildMessageEventHandler;

    @Before
    public void setup() {
        itemsRepository = mock(ItemsRepository.class);
        buildMessageEventHandler = new BuildMessageEventHandler(itemsRepository);
    }

    @AfterEach
    public void tearDown() {
        reset(itemsRepository);
    }


    @Test
    public void whenGivenValidComponentItemEntities_then_returnFullItemEntityInEmbed()
        throws InvalidMessageException {
        ItemEntity componentOne = TestHelper
            .createComponentItemEntity(1, TestHelper.COMPONENT_ITEM_NAME_ONE,
                TestHelper.COMPONENT_ITEM_DESCRIPTION_ONE);
        ItemEntity componentTwo = TestHelper
            .createComponentItemEntity(2, TestHelper.COMPONENT_ITEM_NAME_TWO,
                TestHelper.COMPONENT_ITEM_DESCRIPTION_TWO);
        ItemEntity fullItem = TestHelper
            .createFullItemEntity(3, TestHelper.FULL_ITEM_NAME_ONE,
                TestHelper.FULL_ITEM_DESCRIPTION_ONE, componentOne,
                componentTwo);

        when(itemsRepository
            .findByComponentOneNameAndComponentTwoNameIsInOrComponentTwoNameAndComponentOneNameIsIn(
                anyString(), anyList(), anyString(), anyList()))
            .thenReturn(Collections.singletonList(fullItem));

        String buildCommand = TestHelper
            .buildCommand(Arrays.asList(componentOne, componentTwo), false);
        EmbedBuilder embedBuilder = new EmbedBuilder();
        Map<String, String> filePathMap = new HashMap<>();

        buildMessageEventHandler.handleEmbedMessage(buildCommand, embedBuilder, filePathMap);

        List<Field> embedFields = embedBuilder.getFields();

        assertEquals(1, filePathMap.size());
        assertEquals(1, embedFields.size());
        Field field = embedFields.get(0);
        assertEquals(fullItem.getName(), field.getName());
        assertEquals(TestHelper.buildEmbedFieldDescription(componentOne, componentTwo),
            field.getValue());
    }

    @Test
    public void whenGivenValidComponentItemEntitiesWithDescTag_then_returnFullItemEntityWithDescInEmbed()
        throws InvalidMessageException {
        ItemEntity componentOne = TestHelper
            .createComponentItemEntity(1, TestHelper.COMPONENT_ITEM_NAME_ONE,
                TestHelper.COMPONENT_ITEM_DESCRIPTION_ONE);
        ItemEntity componentTwo = TestHelper
            .createComponentItemEntity(2, TestHelper.COMPONENT_ITEM_NAME_TWO,
                TestHelper.COMPONENT_ITEM_DESCRIPTION_TWO);
        ItemEntity fullItem = TestHelper
            .createFullItemEntity(3, TestHelper.FULL_ITEM_NAME_ONE,
                TestHelper.FULL_ITEM_DESCRIPTION_ONE, componentOne,
                componentTwo);

        when(itemsRepository
            .findByComponentOneNameAndComponentTwoNameIsInOrComponentTwoNameAndComponentOneNameIsIn(
                anyString(), anyList(), anyString(), anyList()))
            .thenReturn(Collections.singletonList(fullItem));

        String buildCommand = TestHelper
            .buildCommand(Arrays.asList(componentOne, componentTwo), true);
        EmbedBuilder embedBuilder = new EmbedBuilder();
        Map<String, String> filePathMap = new HashMap<>();

        buildMessageEventHandler.handleEmbedMessage(buildCommand, embedBuilder, filePathMap);

        List<Field> embedFields = embedBuilder.getFields();

        assertEquals(1, filePathMap.size());
        assertEquals(1, embedFields.size());
        Field field = embedFields.get(0);
        assertEquals(fullItem.getName(), field.getName());
        assertEquals(TestHelper.buildEmbedFieldDescriptionWithItemDesc(componentOne, componentTwo,
            fullItem.getDescription()), field.getValue());
    }

    @Test
    public void whenGivenInvalidBuildCommand_then_throwIllegalArgumentException() {
        String invalidBuildCommand = "!invalid build command";
        EmbedBuilder embedBuilder = new EmbedBuilder();
        Map<String, String> filePathMap = new HashMap<>();

        IllegalArgumentException illegalArgumentException = assertThrows(
            IllegalArgumentException.class, () ->
                buildMessageEventHandler
                    .handleEmbedMessage(invalidBuildCommand, embedBuilder, filePathMap));

        assertEquals(TestHelper.invalidCommandErrorMessage("build", invalidBuildCommand),
            illegalArgumentException.getMessage());
    }

    @Test
    public void whenGivenValidBuildCommandWithNoComponents_then_throwNoArgumentProvidedException() {
        String validBuildCommandWithNoComponents = TestHelper
            .buildCommand(Collections.emptyList(), true);
        EmbedBuilder embedBuilder = new EmbedBuilder();
        Map<String, String> filePathMap = new HashMap<>();

        NoArgumentProvidedException noArgumentProvidedException = assertThrows(
            NoArgumentProvidedException.class, () ->
                buildMessageEventHandler
                    .handleEmbedMessage(validBuildCommandWithNoComponents, embedBuilder,
                        filePathMap));

        assertEquals(TestHelper.validCommandWithNoArgumentsErrorMessage("components"),
            noArgumentProvidedException.getMessage());
    }

    @Test
    public void whenComponentsCannotBeFound_then_throwEntityDoesNotExistException() {
        ItemEntity componentOne = TestHelper
            .createComponentItemEntity(1, TestHelper.COMPONENT_ITEM_NAME_ONE,
                TestHelper.COMPONENT_ITEM_DESCRIPTION_ONE);

        String buildCommand = TestHelper
            .buildCommand(Collections.singletonList(componentOne), true);
        EmbedBuilder embedBuilder = new EmbedBuilder();
        Map<String, String> filePathMap = new HashMap<>();

        when(itemsRepository.findByComponentOneNameOrComponentTwoName(anyString(), anyString()))
            .thenReturn(Collections.emptyList());

        EntityDoesNotExistException entityDoesNotExistException = assertThrows(
            EntityDoesNotExistException.class, () ->
                buildMessageEventHandler
                    .handleEmbedMessage(buildCommand, embedBuilder,
                        filePathMap));

        assertEquals(TestHelper.entityNotFoundErrorMessage("components"),
            entityDoesNotExistException.getMessage());
    }
}
