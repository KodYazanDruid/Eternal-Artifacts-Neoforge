package com.sonamorningstar.eternalartifacts.util;

import com.sonamorningstar.eternalartifacts.EternalArtifacts;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.entity.BlockEntity;

import javax.annotation.Nullable;
import java.util.Optional;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BlockEntityHelper {
    public static <T> Optional<T> get(Class<T> cls, @Nullable BlockGetter world, BlockPos pos) {
        return get(cls, world, pos, false);
    }

    public static <T> Optional<T> get(Class<T> cls, BlockGetter world, BlockPos pos, boolean logging) {
        if(!isBlockLoaded(world, pos)) return Optional.empty();
        BlockEntity entity = world.getBlockEntity(pos);
        if(entity == null) return Optional.empty();
        if(cls.isInstance(entity)) return Optional.of(cls.cast(entity));
        else if(logging) EternalArtifacts.LOGGER.warn("Unexpected BlockEntity class as {}. Expected {}, but found {}", pos, cls, entity.getClass());
        return Optional.empty();
    }

    public static boolean isBlockLoaded(@Nullable BlockGetter world, BlockPos pos) {
        if(world == null) return false;
        if(world instanceof LevelReader) return ((LevelReader) world).hasChunkAt(pos);
        return true;
    }
}
