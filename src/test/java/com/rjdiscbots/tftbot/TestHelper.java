package com.rjdiscbots.tftbot;

import com.rjdiscbots.tftbot.db.champions.ChampionStatsEntity;
import com.rjdiscbots.tftbot.db.champions.ChampionsEntity;
import com.rjdiscbots.tftbot.db.items.ItemEntity;
import com.rjdiscbots.tftbot.utility.DiscordMessageHelper;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class TestHelper {

    public static final String FULL_ITEM_NAME_ONE = "FULL_ITEM_NAME_ONE";
    public static final String FULL_ITEM_DESCRIPTION_ONE = "FULL_ITEM_DESCRIPTION_ONE";

    public static final String COMPONENT_ITEM_NAME_ONE = "COMPONENT_ITEM_NAME_ONE";
    public static final String COMPONENT_ITEM_DESCRIPTION_ONE = "COMPONENT_ITEM_DESCRIPTION_ONE";

    public static final String COMPONENT_ITEM_NAME_TWO = "COMPONENT_ITEM_NAME_TWO";
    public static final String COMPONENT_ITEM_DESCRIPTION_TWO = "COMPONENT_ITEM_DESCRIPTION_TWO";

    public static final String CHAMPION_ID_ONE = "CHAMPION_ID_ONE";
    public static final String CHAMPION_NAME_ONE = "CHAMPION_NAME_ONE";
    public static final String CHAMPION_ABILITY_ONE =
        "{\"desc\": \"CHAMPION ABILITY DESCRIPTION\",\n"
            + "\"name\": \"CHAMPION NAME!\"}";
    public static final List<String> CHAMPION_TRAITS_ONE = Arrays.asList("TRAIT 1", "TRAIT 2");

    public static final String COMP_NAME_ONE = "COMP_NAME_ONE";

    public static final String SYNERGY_NAME_ONE = "SYNERGY_NAME_ONE";

    public static final String GALAXY_NAME_ONE = "GALAXY_NAME_ONE";

    public static ItemEntity createFullItemEntity(Integer id, String name, String description,
        ItemEntity componentOne, ItemEntity componentTwo) {
        ItemEntity itemEntity = new ItemEntity();
        itemEntity.setId(id);
        itemEntity.setName(name);
        itemEntity.setDescription(description);
        itemEntity.setComponentOne(componentOne.getId());
        itemEntity.setComponentOneName(componentOne.getName());
        itemEntity.setComponentTwo(componentTwo.getId());
        itemEntity.setComponentTwoName(componentTwo.getName());
        return itemEntity;
    }

    public static ItemEntity createComponentItemEntity(Integer id, String name,
        String description) {
        ItemEntity itemEntity = new ItemEntity();
        itemEntity.setId(id);
        itemEntity.setName(name);
        itemEntity.setDescription(description);
        return itemEntity;
    }

    public static ChampionsEntity createChampionEntity(String id, String name, int cost,
        List<String> traits, String jsonAbility) {
        ChampionsEntity championsEntity = new ChampionsEntity();
        championsEntity.setId(id);
        championsEntity.setName(name);
        championsEntity.setCost(cost);
        championsEntity.setTraits(traits);
        championsEntity.setAbility(jsonAbility);
        return championsEntity;
    }

    public static ChampionStatsEntity createRandomChampionStatsEntity(String id, String champion) {
        Random random = new Random();
        ChampionStatsEntity championStatsEntity = new ChampionStatsEntity();
        championStatsEntity.setId(id);
        championStatsEntity.setChampion(champion);
        championStatsEntity.setDps(random.nextInt(100));
        championStatsEntity.setAttackSpeed(random.nextDouble());
        championStatsEntity.setDamage(random.nextInt(100));
        championStatsEntity.setRange(random.nextInt(100));
        championStatsEntity.setHealth(random.nextInt(100));
        championStatsEntity.setInitialMana(random.nextInt(100));
        championStatsEntity.setMana(random.nextInt(100));
        championStatsEntity.setMr(random.nextInt(100));
        championStatsEntity.setArmor(random.nextInt(100));
        championStatsEntity.setStars(random.nextInt(3));
        return championStatsEntity;
    }

    public static String buildCommand(List<ItemEntity> itemEntities, boolean addDesc) {
        StringBuilder buildCommand = new StringBuilder("!build ");

        if (addDesc) {
            buildCommand.append("--desc ");
        }
        for (int i = 0; i < itemEntities.size(); i++) {
            ItemEntity itemEntity = itemEntities.get(i);
            if (i != 0) {
                buildCommand.append(", ");
            }
            buildCommand.append(itemEntity.getName());
        }
        return buildCommand.toString();
    }

    public static String buildEmbedFieldDescription(ItemEntity componentOne,
        ItemEntity componentTwo) {
        return componentOne.getName() + ", " + componentTwo.getName();
    }

    public static String buildEmbedFieldDescriptionWithItemDesc(ItemEntity componentOne,
        ItemEntity componentTwo, String desc) {
        StringBuilder itemFieldWithDesc = new StringBuilder();
        itemFieldWithDesc.append(desc);
        itemFieldWithDesc.append(" (")
            .append(buildEmbedFieldDescription(componentOne, componentTwo)).append(")");
        return itemFieldWithDesc.toString();
    }

    public static List<String> championEmbedFieldValues(ChampionsEntity championsEntity,
        ChampionStatsEntity championStatsEntity) {
        List<String> fieldValues = new ArrayList<>();
        fieldValues.add(String.valueOf(championsEntity.getCost()));
        fieldValues.add(DiscordMessageHelper.formatStringList(championsEntity.getTraits()));
        fieldValues.add(String.valueOf(championStatsEntity.getMr()));
        fieldValues.add(String.valueOf(championStatsEntity.getArmor()));
        fieldValues.add(String.valueOf(championStatsEntity.getRange()));
        fieldValues.add(String.valueOf(championStatsEntity.getDamage()));
        fieldValues.add(String.valueOf(championStatsEntity.getHealth()));
        String mana = String.valueOf(championStatsEntity.getInitialMana()).concat("/")
            .concat(String.valueOf(championStatsEntity.getMana()));
        fieldValues.add(mana);
        fieldValues.add(String
            .valueOf(DiscordMessageHelper.formatDouble(championStatsEntity.getAttackSpeed())));
        return fieldValues;
    }

    public static String invalidCommandErrorMessage(String commandType, String commandGiven) {
        return "Message does not begin with !" + commandType + ": "
            + commandGiven;
    }

    public static String validCommandWithNoArgumentsErrorMessage(String missingArg) {
        return "No " + missingArg + " provided!";
    }

    public static String entityNotFoundErrorMessage(String missingArg) {
        return "Invalid " + missingArg + " provided!";
    }
}
