package com.ruoyi.web.controller.sld.converter;

import java.util.HashMap;
import java.util.Map;

public class ConvertMapToUnderLine {
    public static Map<String, Object> convertKeysToUnderscore(Map<String, Object> inputMap) {
        Map<String, Object> convertedMap = new HashMap<>();
        for (Map.Entry<String, Object> entry : inputMap.entrySet()) {
            String key = convertCamelCaseToUnderscore(entry.getKey());
            convertedMap.put(key, entry.getValue());
        }
        return convertedMap;
    }

    public static String convertCamelCaseToUnderscore(String input) {
        StringBuilder output = new StringBuilder();
        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            if (Character.isUpperCase(c)) {
                output.append("_").append(Character.toLowerCase(c));
            } else {
                output.append(c);
            }
        }
        return output.toString();
    }
}
