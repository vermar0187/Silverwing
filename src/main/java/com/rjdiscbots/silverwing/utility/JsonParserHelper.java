package com.rjdiscbots.silverwing.utility;

import com.fasterxml.jackson.databind.JsonNode;
import com.rjdiscbots.silverwing.exceptions.parser.JsonFieldDoesNotExistException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.lang.NonNull;

public class JsonParserHelper {

    public static Map<String, List<JsonNode>> matchingJsonNodesByFieldValue(JsonNode jsonNode1,
        JsonNode jsonNode2, String fieldName) throws JsonFieldDoesNotExistException {
        Map<String, List<JsonNode>> matchingJsonNodes = new HashMap<>();

        for (JsonNode jsonNode : jsonNode1) {
            if (jsonNode.path(fieldName).isMissingNode()) {
                throw new JsonFieldDoesNotExistException("Field" + fieldName + " does not exist");
            }
            String fieldValue = jsonNode.path(fieldName).asText();
            List<JsonNode> jsonNodes = new ArrayList<>();
            jsonNodes.add(jsonNode);
            matchingJsonNodes.put(fieldValue, jsonNodes);
        }

        for (JsonNode jsonNode : jsonNode2) {
            if (jsonNode.path(fieldName).isMissingNode()) {
                throw new JsonFieldDoesNotExistException("Field" + fieldName + " does not exist");
            }
            String fieldValue = jsonNode.path(fieldName).asText();
            if (matchingJsonNodes.containsKey(fieldValue)) {
                matchingJsonNodes.get(fieldValue).add(jsonNode);
            }
        }

        return matchingJsonNodes;
    }

    public static String stringFieldParse(String jsonField, @NonNull JsonNode jsonNode) {
        JsonNode fieldNode = jsonNode.get(jsonField);
        if (fieldNode == null || fieldNode.isNull()) {
            return null;
        } else {
            return fieldNode.asText();
        }
    }

    public static Integer integerFieldParse(String jsonField, @NonNull JsonNode jsonNode) {
        JsonNode fieldNode = jsonNode.get(jsonField);
        if (fieldNode == null || fieldNode.isNull()) {
            return null;
        } else {
            return fieldNode.asInt();
        }
    }

    public static Double doubleFieldParse(String jsonField, @NonNull JsonNode jsonNode) {
        JsonNode fieldNode = jsonNode.get(jsonField);
        if (fieldNode == null || fieldNode.isNull()) {
            return null;
        } else {
            return fieldNode.asDouble();
        }
    }
}
