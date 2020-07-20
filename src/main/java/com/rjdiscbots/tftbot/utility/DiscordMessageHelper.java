package com.rjdiscbots.tftbot.utility;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.WordUtils;
import org.springframework.stereotype.Component;

@Component
public class DiscordMessageHelper {

    public static String formatName(String name) {
        StringBuilder returnName = new StringBuilder(name);

        boolean spaceBefore = true;

        for (int i = 0; i < name.length(); i++) {
            if (spaceBefore) {
                char upperChar = Character.toUpperCase(name.charAt(i));
                returnName.setCharAt(i, upperChar);
                spaceBefore = false;
            } else if (name.charAt(i) == ' ') {
                spaceBefore = true;
            }
        }

        return returnName.toString();
    }
}
