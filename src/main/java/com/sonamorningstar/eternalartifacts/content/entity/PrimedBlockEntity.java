package com.sonamorningstar.eternalartifacts.content.entity;

import com.sonamorningstar.eternalartifacts.core.ModEntities;
import lombok.Setter;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.joml.Vector3d;

import javax.annotation.Nullable;

public class PrimedBlockEntity extends PrimedTnt {
    private BlockState state = Blocks.TNT.defaultBlockState();
    @Setter
    private float radius = 4.0F;
    public PrimedBlockEntity(EntityType<? extends PrimedBlockEntity> entityType, Level pLevel) {
        super(entityType, pLevel);
    }
    public PrimedBlockEntity(Level level, Vector3d pos, @Nullable LivingEntity owner, int fuseTime, float radius, BlockState state) {
        this(ModEntities.PRIMED_BLOCK.get(), level);
        this.setPos(pos.x, pos.y, pos.z);
        double d0 = level.random.nextDouble() * (float) (Math.PI * 2);
        this.setDeltaMovement(-Math.sin(d0) * 0.02, 0.2F, -Math.cos(d0) * 0.02);
        this.xo = pos.x;
        this.yo = pos.y;
        this.zo = pos.z;
        setFuse(fuseTime);
        this.state = state;
        setBlockState(state);
        setRadius(radius);
        this.owner = owner;
    }

    @Override
    protected void explode() {
        this.level().explode(this, this.getX(), this.getY(0.0625), this.getZ(), radius, Level.ExplosionInteraction.TNT);
    }

    @Override
    protected Component getTypeName() {
        return Component.translatable(this.getType().getDescriptionId()+".primed", Component.translatable(this.state.getBlock().getDescriptionId()));
    }
}
