//package com.rjdiscbots.tftbot.discord.message;
//
//import static org.junit.Assert.assertEquals;
//import static org.mockito.Mockito.mock;
//import static org.mockito.Mockito.verify;
//import static org.mockito.Mockito.verifyZeroInteractions;
//import static org.mockito.Mockito.when;
//
//import com.rjdiscbots.tftbot.db.galaxies.GalaxyEntity;
//import com.rjdiscbots.tftbot.db.galaxies.GalaxiesRepository;
//import com.rjdiscbots.tftbot.db.items.ItemsRepository;
//import java.util.ArrayList;
//import java.util.List;
//import net.dv8tion.jda.api.entities.Message;
//import net.dv8tion.jda.api.entities.MessageChannel;
//import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
//import net.dv8tion.jda.api.requests.restaction.MessageAction;
//import org.junit.Test;
//import org.mockito.ArgumentCaptor;
//import org.mockito.Matchers;
//
//public class GalaxyMessageEventHandlerTest {
//
//    @Test
//    public void whenMessageIsReceivedIsGalaxy_then_returnDescripiton() {
//        GalaxiesRepository galaxiesRepository = mock(GalaxiesRepository.class);
//        ItemsRepository itemsRepository = mock(ItemsRepository.class);
//
//        List<GalaxyEntity> galaxiesEntities = new ArrayList<GalaxyEntity>();
//        galaxiesEntities.add(new GalaxyEntity("key", "Binary Star", "binary star description"));
//        when(galaxiesRepository.findByName("binary star")).thenReturn(galaxiesEntities);
//
//        GalaxyMessageEventHandler galaxyMessageEventHandler = new GalaxyMessageEventHandler(
//            galaxiesRepository);
//        ItemMessageEventHandler itemMessageEventHandler = new ItemMessageEventHandler(
//            itemsRepository);
//        MessageReceivedEventHandler messageReceivedEventHandler = new MessageReceivedEventHandler(
//            galaxyMessageEventHandler, itemMessageEventHandler);
//        MessageReceivedEvent messageReceivedEvent = createFakeMessage("!galaxy Binary Star");
//
//        messageReceivedEventHandler.handleMessage(messageReceivedEvent);
//
//        ArgumentCaptor<String> argumentCaptor = new ArgumentCaptor<>();
//        verify(messageReceivedEvent.getChannel()).sendMessage(argumentCaptor.capture());
//
//        assertEquals("binary star description", argumentCaptor.getValue());
//    }
//
//    @Test
//    public void whenMessageIsReceivedIsNotValid_then_doNotSendMessage() {
//        GalaxiesRepository galaxiesRepository = mock(GalaxiesRepository.class);
//        ItemsRepository itemsRepository = mock(ItemsRepository.class);
//
//        GalaxyMessageEventHandler galaxyMessageEventHandler = new GalaxyMessageEventHandler(
//            galaxiesRepository);
//        ItemMessageEventHandler itemMessageEventHandler = new ItemMessageEventHandler(
//            itemsRepository);
//        MessageReceivedEventHandler messageReceivedEventHandler = new MessageReceivedEventHandler(
//            galaxyMessageEventHandler, itemMessageEventHandler);
//        MessageReceivedEvent messageReceivedEvent = createFakeMessage("!galaxybad");
//
//        messageReceivedEventHandler.handleMessage(messageReceivedEvent);
//
//        verifyZeroInteractions(messageReceivedEvent.getChannel());
//    }
//
//    @Test
//    public void whenMessageIsReceivedIsGalaxyButNotFound_then_sendInvalidGalaxyMessage() {
//        GalaxiesRepository galaxiesRepository = mock(GalaxiesRepository.class);
//        ItemsRepository itemsRepository = mock(ItemsRepository.class);
//
//        List<GalaxyEntity> galaxiesEntities = new ArrayList<GalaxyEntity>();
//        when(galaxiesRepository.findByName("Invalid Galaxy")).thenReturn(galaxiesEntities);
//
//        GalaxyMessageEventHandler galaxyMessageEventHandler = new GalaxyMessageEventHandler(
//            galaxiesRepository);
//        ItemMessageEventHandler itemMessageEventHandler = new ItemMessageEventHandler(
//            itemsRepository);
//        MessageReceivedEventHandler messageReceivedEventHandler = new MessageReceivedEventHandler(
//            galaxyMessageEventHandler, itemMessageEventHandler);
//        MessageReceivedEvent messageReceivedEvent = createFakeMessage("!galaxy Invalid Galaxy");
//
//        messageReceivedEventHandler.handleMessage(messageReceivedEvent);
//
//        ArgumentCaptor<String> argumentCaptor = new ArgumentCaptor<>();
//        verify(messageReceivedEvent.getChannel()).sendMessage(argumentCaptor.capture());
//
//        assertEquals("No such galaxy exists!", argumentCaptor.getValue());
//    }
//
//    private MessageReceivedEvent createFakeMessage(String messageText) {
//        Message message = mock(Message.class);
//        when(message.getContentRaw()).thenReturn(messageText);
//
//        MessageAction messageAction = mock(MessageAction.class);
//        MessageChannel messageChannel = mock(MessageChannel.class);
//        when(messageChannel.sendMessage(Matchers.anyString())).thenReturn(messageAction);
//
//        MessageReceivedEvent messageReceivedEvent = mock(MessageReceivedEvent.class);
//        when(messageReceivedEvent.getMessage()).thenReturn(message);
//        when(messageReceivedEvent.getChannel()).thenReturn(messageChannel);
//
//        return messageReceivedEvent;
//    }
//}