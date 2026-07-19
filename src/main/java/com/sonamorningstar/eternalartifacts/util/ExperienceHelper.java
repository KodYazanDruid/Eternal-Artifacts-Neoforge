package com.sonamorningstar.eternalartifacts.util;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantments;
import net.neoforged.neoforge.event.entity.player.PlayerXpEvent;

public class ExperienceHelper {
    
    /**
     *
     * @param stack
     * @param xp
     * @return Remaining xp after mending is applied. If the stack cannot be repaired or has no mending, returns the input xp.
     */
    public static int mendItem(ItemStack stack, int xp) {
        int remainingXp = xp;
        if (!stack.isEmpty() && stack.getEnchantmentLevel(Enchantments.MENDING) > 0) {
            int repairAmount = Math.min(remainingXp * 2, stack.getDamageValue());
            stack.setDamageValue(stack.getDamageValue() - repairAmount);
            remainingXp -= repairAmount / 2;
        }
        return remainingXp;
    }
    
    public static int getTotalPlayerXp(Player player) {
        return (int) (totalXpForLevel(player.experienceLevel)
					+ Math.round(player.experienceProgress * (float) player.getXpNeededForNextLevel()));
    }

    public static int xpRequired(int currLevel) {
        if (currLevel <= 15) return 2 * currLevel + 7;
        else if (currLevel <= 30) return 5 * currLevel - 38;
        else return 9 * currLevel - 158;
    }
    
    public static long totalXpForLevel(int level) {
        long L = level;
        if (level <= 16) {
            return L * L + 6 * L;
        } else if (level <= 31) {
            return (5 * L * L - 81 * L + 720) / 2;
        } else {
            return (9 * L * L - 325 * L + 4440) / 2;
        }
    }
    
    public static int totalLevelsFromXp(long totalXp) {
        if (totalXp < 0) return 0;
        long lo = 0, hi = 1;
        while (totalXpForLevel((int) hi) <= totalXp) {
            hi *= 2;
            if (hi > Integer.MAX_VALUE) { hi = Integer.MAX_VALUE; break; }
        }
        while (lo < hi) {
            long mid = (lo + hi + 1) / 2;
            if (totalXpForLevel((int) mid) <= totalXp) {
                lo = mid;
            } else {
                hi = mid - 1;
            }
        }
        return (int) lo;
    }

    public static void givePlayerXpSilent(Player player, int xp) {
        PlayerXpEvent.XpChange event = new PlayerXpEvent.XpChange(player, xp);
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
