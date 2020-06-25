//package com.rjdiscbots.tftbot.discord;
//
//import static org.mockito.Mockito.mock;
//import static org.mockito.Mockito.verify;
//import static org.mockito.Mockito.when;
//
//import com.rjdiscbots.tftbot.discord.websocket.DiscordWebSocket;
//import net.dv8tion.jda.api.entities.Message;
//import net.dv8tion.jda.api.entities.MessageChannel;
//import net.dv8tion.jda.api.entities.User;
//import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
//import net.dv8tion.jda.api.requests.restaction.MessageAction;
//import org.junit.Before;
//import org.junit.Test;
//import org.mockito.Matchers;
//
//public class DiscordWebSocketTest {
//
//    private DiscordWebSocket discordWebSocket;
//
//    @Before
//    public void setup() {
//        discordWebSocket = new DiscordWebSocket();
//    }
//
//    @Test(expected = NullPointerException.class)
//    public void whenMessageReceivedIsNull_then_throwException() {
//        discordWebSocket.onMessageReceived(null);
//    }
//
//    @Test
//    public void whenMessageReceivedIsValid_then_sendMessage() {
//        MessageReceivedEvent messageReceivedEvent = validMessage();
//        discordWebSocket.onMessageReceived(messageReceivedEvent);
//
//        verify(messageReceivedEvent.getChannel());
//    }
//
//    private MessageReceivedEvent validMessage() {
//        Message message = mock(Message.class);
//        when(message.getContentRaw()).thenReturn("!ping");
//        when(message.getContentDisplay()).thenReturn("!ping");
//
//        User user = mock(User.class);
//        when(user.getName()).thenReturn("User1");
//
//        MessageAction messageAction = mock(MessageAction.class);
//        MessageChannel messageChannel = mock(MessageChannel.class);
//        when(messageChannel.sendMessage(Matchers.anyString())).thenReturn(messageAction);
//
//        MessageReceivedEvent messageReceivedEvent = mock(MessageReceivedEvent.class);
//        when(messageReceivedEvent.getAuthor()).thenReturn(user);
//        when(messageReceivedEvent.getMessage()).thenReturn(message);
//        when(messageReceivedEvent.getChannel()).thenReturn(messageChannel);
//
//        return messageReceivedEvent;
//    }
//}
