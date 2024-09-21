package com.sonamorningstar.eternalartifacts.util;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;

public class ExperienceHelper {

    public static int getTotalPlayerXp(Player player) {
        return (int) ((float) totalXpForLevel(player.experienceLevel) + player.experienceProgress * (float) player.getXpNeededForNextLevel());
    }

    public static int xpRequired(int currLevel) {
        if (currLevel <= 15) return 2 * currLevel + 7;
        else if (currLevel <= 30) return 5 * currLevel - 38;
        else return 9 * currLevel - 158;
    }

    public static int totalXpForLevel(int level) {
        if(level <= 16) return level * level + 6 * level;
        else if(level <= 31) return (int) ( 2.5F * level * level - 40.5F * level + 360);
        else return (int) (4.5F * level * level - 162.5F * level + 2220);
    }

    public static int totalLevelsFromXp(int total) {
        if(total <= 352) return (int) Math.sqrt(total + 9) - 3;
        else if ( total <= 1507) return (int) (8.1D + Math.sqrt(0.4 * (total - 195.975D)));
        else return (int) (325/18D +  Math.sqrt(2/9D * (total - 54215/72D)));
    }

    public static void givePlayerXpSilent(Player player, int xp) {
        net.neoforged.neoforge.event.entity.player.PlayerXpEvent.XpChange event = new net.neoforged.neoforge.event.entity.player.PlayerXpEvent.XpChange(player, xp);
        if (net.neoforged.neoforge.common.NeoForge.EVENT_BUS.post(event).isCanceled()) return;
        xp = event.getAmount();

        player.experienceProgress += (float)xp / (float)player.getXpNeededForNextLevel();
        player.totalExperience = Mth.clamp(player.totalExperience + xp, 0, Integer.MAX_VALUE);

        while(player.experienceProgress < 0.0F) {
            float f = player.experienceProgress * (float)player.getXpNeededForNextLevel();
            if (player.experienceLevel > 0) {
                player.giveExperienceLevels(-1);
                player.experienceProgress = 1.0F + f / (float)player.getXpNeededForNextLevel();
            } else {
                player.giveExperienceLevels(-1);
                player.experienceProgress = 0.0F;
            }
        }

        while(player.experienceProgress >= 1.0F) {
            player.experienceProgress = (player.experienceProgress - 1.0F) * (float)player.getXpNeededForNextLevel();
            player.giveExperienceLevels(1);
            player.experienceProgress /= (float)player.getXpNeededForNextLevel();
        }
    }

    public static int giveXpLevel(Player player, int level) {
        player.experienceLevel += level;
        return level;
    }
    public static int takeXpLevel(Player player, int level) {
        if (level > player.experienceLevel) {
            int taken = player.experienceLevel;
            player.experienceLevel = 0;
            return taken;
        }
        else {
            player.experienceLevel -= level;
            return level;
        }
    }
}
