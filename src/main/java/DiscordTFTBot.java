import javax.annotation.Nonnull;
import javax.security.auth.login.LoginException;
import net.dv8tion.jda.api.AccountType;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class DiscordTFTBot extends ListenerAdapter {

    public static void main(String[] args) throws LoginException {
        JDABuilder jdaBuilder = new JDABuilder(AccountType.BOT);
        String token = "";
        jdaBuilder.setToken(token);
        jdaBuilder.addEventListeners(new DiscordTFTBot());
        jdaBuilder.build();
    }

    @Override
    public void onMessageReceived(@Nonnull MessageReceivedEvent event) {
        System.out.println("We received a message from " +
            event.getAuthor().getName() + ": " +
            event.getMessage().getContentDisplay()
        );

        if (event.getMessage().getContentRaw().equals("!ping")) {
            event.getChannel().sendMessage("Pong!").queue();
        }
    }

}
