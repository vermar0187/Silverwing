package com.rjdiscbots.tftbot.utility;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.rjdiscbots.tftbot.db.compositions.CompositionItemsEntity;
import com.rjdiscbots.tftbot.db.synergies.SetModel;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

@Component
public class DiscordMessageHelper {

    private static ObjectMapper objectMapper = new ObjectMapper();

    public static String formatName(String name) {
        StringBuilder returnName = new StringBuilder(name);

        boolean needCapitilization = true;

        for (int i = 0; i < name.length(); i++) {
            if (name.charAt(i) == ' ' || name.charAt(i) == '.') {
                needCapitilization = true;
            } else if (needCapitilization) {
                char upperChar = Character.toUpperCase(name.charAt(i));
                returnName.setCharAt(i, upperChar);
                needCapitilization = false;
            }
        }

        return returnName.toString();
    }

    public static Double formatDouble(Double dbl) {
        DecimalFormat decimalFormat = new DecimalFormat("#.##");
        return Double.valueOf(decimalFormat.format(dbl));
    }

    public static String formatStringList(List<String> strs) {
        StringBuilder stringBuilder = new StringBuilder();

        for (int i = 0; i < strs.size(); i++) {
            String str = strs.get(i);
            if (i != 0) {
                stringBuilder.append(", ");
            }
            stringBuilder.append(formatName(str));
        }

        return stringBuilder.toString();
    }

    public static String formatAbilityDoubleList(List<Double> doubleList, boolean isPercent) {
        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append("**(");

        boolean sameDouble = true;
        Double initVal = !doubleList.isEmpty() ? doubleList.get(0) : 0.0;

        for (int i = 0; i < doubleList.size(); i++) {
            Double val = doubleList.get(i);
            if (i != 0) {
                stringBuilder.append("/");
            }

            if (!initVal.equals(val)) {
                sameDouble = false;
            }

            if (isPercent) {
                val = val * 100.00;
            }

            stringBuilder.append(formatDouble(val));
        }
        stringBuilder.append(")**");

        if (sameDouble) {
            stringBuilder.setLength(0);
            initVal = isPercent ? initVal * 100 : initVal;
            stringBuilder.append(initVal);
        }

        return stringBuilder.toString();
    }

    public static String formatAbility(String jsonAbility)
        throws JsonProcessingException, NullPointerException {
        StringBuilder stringBuilder = new StringBuilder();
        JsonNode jsonNode;

        jsonNode = objectMapper.readTree(jsonAbility);

        JsonNode nameNode = jsonNode.get("name");
        JsonNode descriptionNode = jsonNode.get("desc");

        if (nameNode == null || descriptionNode == null) {
            throw new NullPointerException();
        }

        String abilityName = nameNode.asText();
        String abilityDesc = descriptionNode.asText();

        stringBuilder.append("**").append(abilityName).append("**").append(": ");

        String[] variableTags = StringUtils.substringsBetween(abilityDesc, "@", "@");

        JsonNode variables = jsonNode.get("variables");

        if (variables != null && variables.isArray()) {
            ArrayNode varArr = (ArrayNode) variables;
            for (JsonNode var : varArr) {
                String variableName = var.get("name").asText();

                JsonNode valueNode = var.get("value");
                List<Double> values = new ArrayList<>();
                if (valueNode.isArray()) {
                    ArrayNode valueArr = (ArrayNode) valueNode;

                    if (valueArr.size() >= 4) {
                        for (int i = 1; i < 4; i++) {
                            Double dblVal = valueArr.get(i).asDouble();
                            values.add(dblVal);
                        }
                    }
                }

                for (String tag : variableTags) {
                    if (StringUtils.contains(tag, variableName)) {
                        String doubleListStr = formatAbilityDoubleList(values,
                            StringUtils.contains(tag, "100"));
                        abilityDesc = abilityDesc.replaceFirst(tag, doubleListStr);
                    }
                }
            }
        }

        stringBuilder.append(abilityDesc.replaceAll("@", ""));

        return stringBuilder.toString();
    }

    public static Map<String, List<CompositionItemsEntity>> formatCompositionItemsByStage(
        List<CompositionItemsEntity> compositionItemsEntities) {
        Map<String, List<CompositionItemsEntity>> stageMap = new HashMap<>();

        if (compositionItemsEntities == null || compositionItemsEntities.isEmpty()) {
            return stageMap;
        }

        for (CompositionItemsEntity compositionItemsEntity : compositionItemsEntities) {
            String stage = compositionItemsEntity.getStage();

            if (stageMap.containsKey(stage)) {
                stageMap.get(stage).add(compositionItemsEntity);
            } else {
                List<CompositionItemsEntity> champions = new ArrayList<>();
                champions.add(compositionItemsEntity);
                stageMap.put(stage, champions);
            }
        }

        return stageMap;
    }

    public static String formatSetModel(List<SetModel> models) {
        StringBuilder medal = new StringBuilder();
        for (int i = 0; i < models.size(); i++) {
            SetModel setModel = models.get(i);
            if (i != 0) {
                medal.append(", ");
            }

            String style = setModel.getStyle();
            style = style.substring(0, 1).toUpperCase() + style.substring(1);
            Integer min = setModel.getMin();
            Integer max = setModel.getMax();

            medal.append(style).append(" (");

            if (min != null && max != null) {
                medal.append(min).append("-").append(max).append(")");
            } else if (min != null) {
                medal.append(min).append(")");
            } else if (max != null) {
                medal.append(max).append(")");
            }
        }
        return medal.toString();
    }
}
