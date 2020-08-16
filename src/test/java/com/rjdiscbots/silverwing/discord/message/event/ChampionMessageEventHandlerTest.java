package com.rjdiscbots.silverwing.discord.message.event;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;

import com.rjdiscbots.silverwing.TestHelper;
import com.rjdiscbots.silverwing.db.champions.ChampionStatsEntity;
import com.rjdiscbots.silverwing.db.champions.ChampionStatsRepository;
import com.rjdiscbots.silverwing.db.champions.ChampionsEntity;
import com.rjdiscbots.silverwing.db.champions.ChampionsRepository;
import com.rjdiscbots.silverwing.exceptions.message.EntityDoesNotExistException;
import com.rjdiscbots.silverwing.exceptions.message.InvalidMessageException;
import com.rjdiscbots.silverwing.exceptions.message.NoArgumentProvidedException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed.Field;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.AfterEach;

public class ChampionMessageEventHandlerTest {

    private ChampionsRepository championsRepository;

    private ChampionStatsRepository championStatsRepository;

    private ChampionMessageEventHandler championMessageEventHandler;

    @Before
    public void setup() {
        championsRepository = mock(ChampionsRepository.class);
        championStatsRepository = mock(ChampionStatsRepository.class);
        championMessageEventHandler = new ChampionMessageEventHandler(championsRepository,
            championStatsRepository);
    }

    @AfterEach
    public void tearDown() {
        reset(championsRepository);
        reset(championStatsRepository);
    }

    @Test
    public void whenGivenChampionCommand_then_addToChampionEmbed() throws InvalidMessageException {
        ChampionsEntity championsEntity = TestHelper
            .createChampionEntity(TestHelper.CHAMPION_ID_ONE, TestHelper.CHAMPION_NAME_ONE, 3,
                TestHelper.CHAMPION_TRAITS_ONE, TestHelper.CHAMPION_ABILITY_ONE);
        ChampionStatsEntity championStatsEntity = TestHelper
            .createRandomChampionStatsEntity(championsEntity.getId(), championsEntity.getName());

        String championCommand = "!champion " + championsEntity.getName();
        EmbedBuilder embedBuilder = new EmbedBuilder();
        Map<String, String> filePathMap = new HashMap<>();

        when(championsRepository.findOneByName(championsEntity.getName()))
            .thenReturn(championsEntity);
        when(championStatsRepository.findOneByChampionOrderByStarsAsc(championsEntity.getName()))
            .thenReturn(championStatsEntity);

        championMessageEventHandler.handleEmbedMessage(championCommand, embedBuilder, filePathMap);

        List<Field> fields = embedBuilder.getFields();

        assertEquals(filePathMap.size(), 1);
        assertEquals(fields.size(), 9);

        List<String> fieldValues = fields.stream().map(Field::getValue)
            .collect(Collectors.toList());
        List<String> championStringValues = TestHelper
            .championEmbedFieldValues(championsEntity, championStatsEntity);
        Collections.sort(fieldValues);
        Collections.sort(championStringValues);
        assertEquals(fieldValues, championStringValues);
    }

    @Test
    public void whenGivenInvalidChampionCommand_then_throwIllegalArgumentException() {
        String invalidChampionCommand = "!invalid champion command";
        EmbedBuilder embedBuilder = new EmbedBuilder();
        Map<String, String> filePathMap = new HashMap<>();

        IllegalArgumentException illegalArgumentException = assertThrows(
            IllegalArgumentException.class, () ->
                championMessageEventHandler
                    .handleEmbedMessage(invalidChampionCommand, embedBuilder, filePathMap));

        assertEquals(TestHelper.invalidCommandErrorMessage("champion", invalidChampionCommand),
            illegalArgumentException.getMessage());
    }

    @Test
    public void whenGivenValidChampionCommandWithNoChampions_then_throwNoArgumentProvidedException() {
        String championCommandWithNoChampion = "!champion";
        EmbedBuilder embedBuilder = new EmbedBuilder();
        Map<String, String> filePathMap = new HashMap<>();

        NoArgumentProvidedException noArgumentProvidedException = assertThrows(
            NoArgumentProvidedException.class, () ->
                championMessageEventHandler
                    .handleEmbedMessage(championCommandWithNoChampion, embedBuilder,
                        filePathMap));

        assertEquals(TestHelper.validCommandWithNoArgumentsErrorMessage("champion"),
            noArgumentProvidedException.getMessage());
    }

    @Test
    public void whenChampionCannotBeFound_then_throwEntityDoesNotExistException() {
        String championCommand = "!champion " + TestHelper.CHAMPION_NAME_ONE;
        EmbedBuilder embedBuilder = new EmbedBuilder();
        Map<String, String> filePathMap = new HashMap<>();

        when(championsRepository.findOneByName(TestHelper.CHAMPION_NAME_ONE))
            .thenReturn(null);

        EntityDoesNotExistException entityDoesNotExistException = assertThrows(
            EntityDoesNotExistException.class, () ->
                championMessageEventHandler
                    .handleEmbedMessage(championCommand, embedBuilder,
                        filePathMap));

        assertEquals(TestHelper.entityNotFoundErrorMessage("champion"),
            entityDoesNotExistException.getMessage());
    }
}
