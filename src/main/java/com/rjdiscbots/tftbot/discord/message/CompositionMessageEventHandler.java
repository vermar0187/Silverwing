package com.rjdiscbots.tftbot.discord.message;

import com.rjdiscbots.tftbot.db.compositions.CompositionEntity;
import com.rjdiscbots.tftbot.db.compositions.CompositionItemsEntity;
import com.rjdiscbots.tftbot.db.compositions.CompositionItemsRepository;
import com.rjdiscbots.tftbot.db.compositions.CompositionRepository;
import com.rjdiscbots.tftbot.db.items.ItemEntity;
import com.rjdiscbots.tftbot.db.items.ItemsRepository;
import com.rjdiscbots.tftbot.exceptions.message.EntityDoesNotExistException;
import com.rjdiscbots.tftbot.exceptions.message.InvalidMessageException;
import com.rjdiscbots.tftbot.utility.DiscordMessageHelper;
import java.util.List;
import java.util.Map;
import net.dv8tion.jda.api.EmbedBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

@Component
public class CompositionMessageEventHandler {

    private CompositionRepository compositionRepository;

    private CompositionItemsRepository compositionItemsRepository;

    private ItemsRepository itemsRepository;

    @Autowired
    public CompositionMessageEventHandler(CompositionRepository compositionRepository,
        CompositionItemsRepository compositionItemsRepository,
        ItemsRepository itemsRepository) {
        this.compositionRepository = compositionRepository;
        this.compositionItemsRepository = compositionItemsRepository;
        this.itemsRepository = itemsRepository;
    }

    public void handleEmbedCompositionMessage(@NonNull String rawCompositionMessage,
        @NonNull EmbedBuilder embedBuilder, @NonNull Map<String, String> filePathMap)
        throws InvalidMessageException {
        if (!rawCompositionMessage.startsWith("!comp ")) {
            throw new IllegalArgumentException(
                "Message does begin with !comp: " + rawCompositionMessage);
        }

        rawCompositionMessage = rawCompositionMessage.replaceFirst("!comp ", "");
        rawCompositionMessage = rawCompositionMessage.trim();

        fetchComposition(rawCompositionMessage, embedBuilder);

        String picUrl = "pengu.png";
        filePathMap.put(picUrl, "patch/" + picUrl);
        embedBuilder.setThumbnail("attachment://" + picUrl);
    }

    private void fetchComposition(String compositionName, EmbedBuilder embedBuilder)
        throws EntityDoesNotExistException {
        CompositionEntity composition = compositionRepository.findOneByName(compositionName);

        if (composition == null) {
            throw new EntityDoesNotExistException(
                "Invalid composition provided! Please use the command: **!list comps**");
        }

        String formattedCompositionName = DiscordMessageHelper.formatName(composition.getName());
        String formattedCompositionStrategy = DiscordMessageHelper
            .formatName(composition.getCompStrategy());

        String earlyComposition = DiscordMessageHelper.formatStringList(composition.getBegComp());
        String midComposition = DiscordMessageHelper.formatStringList(composition.getMidComp());
        String endComposition = DiscordMessageHelper.formatStringList(composition.getEndComp());

        List<CompositionItemsEntity> compositionItemsEntities = compositionItemsRepository
            .findByCompName(compositionName);

        Map<String, List<CompositionItemsEntity>> compositionItemsByStage = DiscordMessageHelper
            .formatCompositionItemsByStage(compositionItemsEntities);

        String begItems = fetchChampionItemsForStage("early", compositionItemsByStage);
        String midItems = fetchChampionItemsForStage("mid", compositionItemsByStage);
        String lateItems = fetchChampionItemsForStage("late", compositionItemsByStage);

        embedBuilder.setTitle(formattedCompositionName);
        embedBuilder.setDescription(formattedCompositionStrategy);
        embedBuilder.addField("Early Game", earlyComposition, true);
        embedBuilder.addField("Mid Game", midComposition, true);
        embedBuilder.addField("Late Game", endComposition, true);
        embedBuilder.addField("Early Game Item Distribution", begItems, false);
        embedBuilder.addField("Mid Game Item Distribution", midItems, false);
        embedBuilder.addField("Late Game Item Distribution", lateItems, false);
    }

    private String fetchChampionItemsForStage(String stage,
        Map<String, List<CompositionItemsEntity>> champions) {
        StringBuilder championItems = new StringBuilder();

        List<CompositionItemsEntity> compositionItemsEntities = champions.get(stage);

        if (compositionItemsEntities == null) {
            return "";
        }

        for (CompositionItemsEntity compositionItemsEntity : compositionItemsEntities) {
            String formattedChampionName = DiscordMessageHelper
                .formatName(compositionItemsEntity.getChampionName());
            championItems.append(formattedChampionName).append(" => ");
            List<Integer> itemIds = compositionItemsEntity.getItems();

            for (int i = 0; i < itemIds.size(); i++) {
                Integer id = itemIds.get(i);
                if (i != 0) {
                    championItems.append(", ");
                }
                ItemEntity itemEntity = itemsRepository.findOneById(id);
                String formattedItemName = DiscordMessageHelper.formatName(itemEntity.getName());
                championItems.append(formattedItemName);
            }
            championItems.append("\n");
        }

        return championItems.toString();
    }
}
