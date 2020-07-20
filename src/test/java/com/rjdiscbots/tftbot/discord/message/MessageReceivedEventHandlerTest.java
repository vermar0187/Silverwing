package com.rjdiscbots.tftbot.discord.message;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import com.rjdiscbots.tftbot.db.champions.ChampionStatsRepository;
import com.rjdiscbots.tftbot.db.champions.ChampionsRepository;
import com.rjdiscbots.tftbot.db.galaxies.GalaxiesRepository;
import com.rjdiscbots.tftbot.db.galaxies.GalaxyEntity;
import com.rjdiscbots.tftbot.db.items.ItemsRepository;
import com.rjdiscbots.tftbot.db.synergies.SynergyRepository;
import java.util.ArrayList;
import java.util.List;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.requests.restaction.MessageAction;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.AfterEach;
import org.mockito.ArgumentCaptor;
import org.mockito.Matchers;

public class MessageReceivedEventHandlerTest {

    private MessageReceivedEventHandler messageReceivedEventHandler;

    private ItemMessageEventHandler itemMessageEventHandler;

    private GalaxyMessageEventHandler galaxyMessageEventHandler;

    private ListMessageEventHandler listMessageEventHandler;

    private BuildMessageEventHandler buildMessageEventHandler;

    private SynergyMessageEventHandler synergyMessageEventHandler;

    private ChampionMessageEventHandler championMessageEventHandler;

    private GalaxiesRepository galaxiesRepository;

    private ItemsRepository itemsRepository;

    private SynergyRepository synergyRepository;

    private ChampionsRepository championsRepository;

    private ChampionStatsRepository championStatsRepository;

    @Before
    public void setup() {
        galaxiesRepository = mock(GalaxiesRepository.class);
        itemsRepository = mock(ItemsRepository.class);
        synergyRepository = mock(SynergyRepository.class);
        championsRepository = mock(ChampionsRepository.class);
        championStatsRepository = mock(ChampionStatsRepository.class);

        galaxyMessageEventHandler = new GalaxyMessageEventHandler(galaxiesRepository);
        itemMessageEventHandler = new ItemMessageEventHandler(itemsRepository);
        buildMessageEventHandler = new BuildMessageEventHandler(itemsRepository);
        listMessageEventHandler = new ListMessageEventHandler(itemsRepository, galaxiesRepository,
            synergyRepository);
        synergyMessageEventHandler = new SynergyMessageEventHandler(synergyRepository,
            championsRepository);
        championMessageEventHandler = new ChampionMessageEventHandler(championsRepository,
            championStatsRepository);

        messageReceivedEventHandler = new MessageReceivedEventHandler(galaxyMessageEventHandler,
            itemMessageEventHandler, buildMessageEventHandler, listMessageEventHandler,
            synergyMessageEventHandler, championMessageEventHandler);
    }

    @AfterEach
    public void destroy() {
        reset(galaxiesRepository);
        reset(itemsRepository);
        reset(synergyRepository);
    }

    @Test
    public void whenMessageIsReceivedIsGalaxy_then_returnDescription() {
        List<GalaxyEntity> galaxiesEntities = new ArrayList<GalaxyEntity>();
        galaxiesEntities.add(new GalaxyEntity("key", "Binary Star", "binary star description"));
        when(galaxiesRepository.findByName("binary star")).thenReturn(galaxiesEntities);

        MessageReceivedEvent messageReceivedEvent = createFakeMessage("!galaxy Binary Star");

        messageReceivedEventHandler.handleMessage(messageReceivedEvent);

        ArgumentCaptor<String> argumentCaptor = new ArgumentCaptor<>();
        verify(messageReceivedEvent.getChannel()).sendMessage(argumentCaptor.capture());

        assertEquals("binary star description", argumentCaptor.getValue());
    }

    @Test
    public void whenMessageIsReceivedIsNotValid_then_doNotSendMessage() {
        MessageReceivedEvent messageReceivedEvent = createFakeMessage("!galaxybad");

        messageReceivedEventHandler.handleMessage(messageReceivedEvent);

        verifyZeroInteractions(messageReceivedEvent.getChannel());
    }

    @Test
    public void whenMessageIsReceivedIsGalaxyButNotFound_then_sendInvalidGalaxyMessage() {
        List<GalaxyEntity> galaxiesEntities = new ArrayList<GalaxyEntity>();
        when(galaxiesRepository.findByName("Invalid Galaxy")).thenReturn(galaxiesEntities);

        MessageReceivedEvent messageReceivedEvent = createFakeMessage("!galaxy Invalid Galaxy");

        messageReceivedEventHandler.handleMessage(messageReceivedEvent);

        ArgumentCaptor<String> argumentCaptor = new ArgumentCaptor<>();
        verify(messageReceivedEvent.getChannel()).sendMessage(argumentCaptor.capture());

        assertEquals("No such galaxy exists!", argumentCaptor.getValue());
    }

    private MessageReceivedEvent createFakeMessage(String messageText) {
        Message message = mock(Message.class);
        when(message.getContentRaw()).thenReturn(messageText);

        MessageAction messageAction = mock(MessageAction.class);
        MessageChannel messageChannel = mock(MessageChannel.class);
        when(messageChannel.sendMessage(Matchers.anyString())).thenReturn(messageAction);

        MessageReceivedEvent messageReceivedEvent = mock(MessageReceivedEvent.class);
        when(messageReceivedEvent.getMessage()).thenReturn(message);
        when(messageReceivedEvent.getChannel()).thenReturn(messageChannel);

        return messageReceivedEvent;
    }
}