package com.sonamorningstar.eternalartifacts.util;

public class TooltipHelper {
    public static String prettyName(String path) {
        String displayName = path.replace('_', ' ');
        String[] pathWords = displayName.split("\\s");
        StringBuilder prettyPath = new StringBuilder();
        for(String word : pathWords) prettyPath.append(Character.toTitleCase(word.charAt(0))).append(word.substring(1)).append(" ");
        return prettyPath.toString().trim();
    }
}
