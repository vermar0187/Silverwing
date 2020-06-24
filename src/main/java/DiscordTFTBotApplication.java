import discord.DiscordWebSocketBuilder;

public class DiscordTFTBotApplication {

    public static void main(String[] args) throws Exception {
        String token = System.getenv("TFT_TOKEN");
        DiscordWebSocketBuilder discordTFTBot = new DiscordWebSocketBuilder(token);
    }
}