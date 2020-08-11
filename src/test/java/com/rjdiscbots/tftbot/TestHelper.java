package com.rjdiscbots.tftbot;

import com.rjdiscbots.tftbot.db.items.ItemEntity;
import java.util.List;

public class TestHelper {

    public static final String FULL_ITEM_NAME_ONE = "FULL_ITEM_NAME_ONE";
    public static final String FULL_ITEM_DESCRIPTION_ONE = "FULL_ITEM_DESCRIPTION_ONE";

    public static final String COMPONENT_ITEM_NAME_ONE = "COMPONENT_ITEM_NAME_ONE";
    public static final String COMPONENT_ITEM_DESCRIPTION_ONE = "COMPONENT_ITEM_DESCRIPTION_ONE";

    public static final String COMPONENT_ITEM_NAME_TWO = "COMPONENT_ITEM_NAME_TWO";
    public static final String COMPONENT_ITEM_DESCRIPTION_TWO = "COMPONENT_ITEM_DESCRIPTION_TWO";

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
