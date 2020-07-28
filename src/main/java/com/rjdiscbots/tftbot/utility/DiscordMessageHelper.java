package com.rjdiscbots.tftbot.utility;

import java.math.BigDecimal;
import java.math.RoundingMode;
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

    public static Double formatDouble(Double dbl) {
        return BigDecimal.valueOf(dbl).setScale(3, RoundingMode.HALF_EVEN).doubleValue();
    }
}
