package com.sonamorningstar.eternalartifacts.util;

import com.mojang.authlib.GameProfile;
import com.sonamorningstar.eternalartifacts.api.ModFakePlayer;
import com.sonamorningstar.eternalartifacts.content.block.entity.base.Machine;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.util.FakePlayer;
import net.neoforged.neoforge.event.level.LevelEvent;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Mod.EventBusSubscriber()
public class FakePlayerHelper {
    private static final GameProfile MOD_PROFILE = new GameProfile(UUID.fromString("6832a398-12c4-42bb-924c-dd8a14eb15f6"), "EternalArtifacts");
    private static final Map<LevelAccessor, FakePlayer> levelFakePlayers = new HashMap<>();
    private static final Map<Machine<?>, FakePlayer> machineFakePlayers = new HashMap<>();

    public static FakePlayer getFakePlayer(Level level) {
        return FakePlayerHelper.levelFakePlayers.computeIfAbsent(level, sLevel -> {
            if(level instanceof ServerLevel sl)
                return new ModFakePlayer(sl, MOD_PROFILE, null);
            else
                return null;
        });
    }
    public static FakePlayer getFakePlayer(Machine<?> machine, Level level) {
        return FakePlayerHelper.machineFakePlayers.computeIfAbsent(machine, mMachine -> {
            if(level instanceof ServerLevel sl)
                return new ModFakePlayer(sl, getProfileForMachine(mMachine), mMachine);
            else
                return null;
        });
    }
    
    private static GameProfile getProfileForMachine(Machine<?> machine) {
        String path = BuiltInRegistries.BLOCK_ENTITY_TYPE.getKey(machine.getType()).getPath();
        String name = "EternalArtifacts"+TooltipHelper.prettyNameNoBlanks(path);
        return new GameProfile(UUID.nameUUIDFromBytes(name.getBytes(StandardCharsets.UTF_8)), name);
    }
    
    public static void removeFakePlayer(Machine<?> machine) {
        FakePlayer fakePlayer = machineFakePlayers.remove(machine);
        if(fakePlayer != null) {
            fakePlayer.discard();
        }
    }

    @SubscribeEvent
    public static void onUnload(LevelEvent.Unload event) {
        LevelAccessor level = event.getLevel();
        levelFakePlayers.entrySet().removeIf(entry -> entry.getValue().level() == level);
        machineFakePlayers.entrySet().removeIf(entry -> entry.getValue().level() == level);
    }
}
