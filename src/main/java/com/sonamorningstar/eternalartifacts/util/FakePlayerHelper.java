package com.sonamorningstar.eternalartifacts.util;

import com.mojang.authlib.GameProfile;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.util.FakePlayer;
import net.neoforged.neoforge.common.util.FakePlayerFactory;
import net.neoforged.neoforge.event.level.LevelEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Mod.EventBusSubscriber()
public class FakePlayerHelper {
    private static final GameProfile MOD_PROFILE = new GameProfile(UUID.fromString("6832a398-12c4-42bb-924c-dd8a14eb15f6"), "[EternalArtifacts]");
    private static Map<LevelAccessor, FakePlayer> instances = new HashMap<>();

    public static FakePlayer getFakePlayer(Level level) {
        return FakePlayerHelper.instances.computeIfAbsent(level, serverLevel -> {
            if(serverLevel instanceof ServerLevel)
                return FakePlayerFactory.get((ServerLevel)serverLevel, MOD_PROFILE);
            else
                return null;
        });
    }

    @SubscribeEvent
    public static void onUnload(LevelEvent.Unload event) {
        LevelAccessor level = event.getLevel();
        if(level instanceof ServerLevel) FakePlayerHelper.instances.remove(level);
    }
}
