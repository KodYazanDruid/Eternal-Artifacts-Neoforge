package com.sonamorningstar.eternalartifacts.util;

public class ExperienceHelper {

    public static double xpRequired(int currLevel) {
        if(currLevel <= 0) return 2 * currLevel + 7;
        if(currLevel < 16) {
            return 2 * currLevel + 7;
        } else if( currLevel < 31) {
            return  5 * currLevel - 38;
        } else {
            return 9 * currLevel - 158;
        }
    }

    public static double totalXpForLevel(int level) {
        if(level <= 0) return 0;
        if(level < 17) {
            return level * level + 6 * level;
        }else if ( level < 32) {
            return 2.5 * level * level - 40.5 * level + 360;
        } else {
            return 4.5 * level * level - 162.5 * level + 2220;
        }
    }

    public static double totalLevelsFromXp(int total) {
        if(total <= 0) return 0;
        if(total < 17) {
            return Math.sqrt(total + 9) - 3;
        }else if ( total < 32) {
            return 8.1 + Math.sqrt(0.4 * (total - 195.975));
        } else {
            return 325/18F +  Math.sqrt(2/9F * (total - 54215/72F));
        }
    }
}
