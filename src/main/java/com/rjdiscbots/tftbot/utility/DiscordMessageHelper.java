package com.rjdiscbots.tftbot.utility;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
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
        return BigDecimal.valueOf(dbl).setScale(3, RoundingMode.HALF_EVEN).doubleValue();
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

            stringBuilder.append(val);
        }
        stringBuilder.append(")**");

        if (sameDouble) {
            stringBuilder.setLength(0);
            initVal = isPercent ? initVal * 100 : initVal;
            stringBuilder.append(initVal);
        }

        return stringBuilder.toString();
    }

    public static String formatAbility(String jsonAbility) throws JsonProcessingException {
        StringBuilder stringBuilder = new StringBuilder();
        JsonNode jsonNode;

        jsonNode = objectMapper.readTree(jsonAbility);

        String abilityName = jsonNode.get("name").asText();
        stringBuilder.append("**").append(abilityName).append("**").append(": ");

        String abilityDesc = jsonNode.get("desc").asText();
        String[] variableTags = StringUtils.substringsBetween(abilityDesc, "@", "@");

        JsonNode variables = jsonNode.get("variables");

        if (variables.isArray()) {
            ArrayNode varArr = (ArrayNode) variables;
            for (JsonNode var : varArr) {
                String variableName = var.get("name").asText();

                JsonNode valueNode = var.get("value");
                List<Double> values = new ArrayList<>();
                if (valueNode.isArray()) {
                    ArrayNode valueArr = (ArrayNode) valueNode;

                    if (valueArr.size() >= 4) {
                        for (int i = 1; i < 4; i++) {
                            Double dblVal = formatDouble(valueArr.get(i).asDouble());
                            values.add(dblVal);
                        }
                    }
                }

                for (String tag : variableTags) {
                    if (StringUtils.contains(tag, variableName)) {
                        System.out.println(variableName + " vs. " + tag);
                        String doubleListStr = formatAbilityDoubleList(values,
                            StringUtils.contains(tag, "100"));
                        System.out.println(tag + " vs. " + doubleListStr);
                        abilityDesc = abilityDesc.replaceFirst(tag, doubleListStr);
                    }
                }
            }
        }

        stringBuilder.append(abilityDesc.replaceAll("@", ""));

        return stringBuilder.toString();
    }
}
