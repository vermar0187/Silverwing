package com.rjdiscbots.tftbot.discord.message.event;

import com.rjdiscbots.tftbot.db.compositions.CompositionEntity;
import com.rjdiscbots.tftbot.db.compositions.CompositionItemsEntity;
import com.rjdiscbots.tftbot.db.compositions.CompositionItemsRepository;
import com.rjdiscbots.tftbot.db.compositions.CompositionRepository;
import com.rjdiscbots.tftbot.db.items.ItemsRepository;
import com.rjdiscbots.tftbot.exceptions.message.EntityDoesNotExistException;
import com.rjdiscbots.tftbot.exceptions.message.InvalidMessageException;
import com.rjdiscbots.tftbot.exceptions.message.NoArgumentProvidedException;
import com.rjdiscbots.tftbot.utility.DiscordMessageHelper;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import net.dv8tion.jda.api.EmbedBuilder;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

@Component
public class CompositionMessageEventHandler implements MessageEvent {

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

    @Override
    public void handleEmbedMessage(@NonNull String rawCompositionMessage,
        @NonNull EmbedBuilder embedBuilder, @NonNull Map<String, String> filePathMap)
        throws InvalidMessageException {
        if (!rawCompositionMessage.startsWith("!comp")) {
            throw new IllegalArgumentException(
                "Message does not begin with !comp: " + rawCompositionMessage);
        }

        String compositionName = rawCompositionMessage.replaceFirst("!comp", "");
        compositionName = compositionName.trim();

        fetchComposition(compositionName, embedBuilder);

        String picUrl = "pengu.png";
        filePathMap.put(picUrl, "patch/" + picUrl);
        embedBuilder.setThumbnail("attachment://" + picUrl);
    }

    private void fetchComposition(String compositionName, EmbedBuilder embedBuilder)
        throws EntityDoesNotExistException, NoArgumentProvidedException {
        if (compositionName == null || StringUtils.isAllBlank(compositionName)) {
            throw new NoArgumentProvidedException("No composition provided!");
        }

        CompositionEntity composition = compositionRepository.findOneByName(compositionName);

        if (composition == null) {
            throw new EntityDoesNotExistException("Invalid composition provided!");
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
            return "No necessary items";
        }

        for (CompositionItemsEntity compositionItemsEntity : compositionItemsEntities) {
            String formattedChampionName = DiscordMessageHelper
                .formatName(compositionItemsEntity.getChampionName());
            championItems.append(formattedChampionName).append(" => ");
            List<Integer> itemIds = compositionItemsEntity.getItems();

            List<String> itemNames = itemIds.stream().map((id) -> itemsRepository.findOneById(id))
                .map((itemEntity -> DiscordMessageHelper.formatName(itemEntity.getName())))
                .collect(Collectors.toList());

            String formattedItemList = DiscordMessageHelper.formatStringList(itemNames);

            championItems.append(formattedItemList).append("\n");
        }

        return championItems.toString();
    }
}
