package com.sonamorningstar.eternalartifacts.content.spell;

import com.sonamorningstar.eternalartifacts.content.spell.base.Spell;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.EvokerFangs;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;

public class EvokerFangsSpell extends Spell {
    public EvokerFangsSpell(Properties props) {
        super(props);
    }

    @Override
    public boolean cast(ItemStack stack, Level level, LivingEntity caster) {
        if (!(caster instanceof Player) || (caster instanceof Player player && !player.getCooldowns().isOnCooldown(stack.getItem()))){
            Vec3 looking = caster.getLookAngle();
            Vec3[] all = new Vec3[]{looking, looking.yRot(0.22f), looking.yRot(-0.22f)};
            for (Vec3 vector3d : all) {
                float f = (float) Mth.atan2(vector3d.z, vector3d.x);
                for (int i = 0; i < 8; i++) {
                    double d2 = 1.25D * (double) (i + 1);
                    this.spawnFangs(caster, caster.getX() + (double) Mth.cos(f) * d2, caster.getZ() + (double) Mth.sin(f) * d2, caster.getY() - 1, caster.getY() + 1, f, i * 5);
                }
            }
            if (caster instanceof Player player && cooldown > 0) player.getCooldowns().addCooldown(stack.getItem(), cooldown);
            return true;
        }
        return false;
    }

    private void spawnFangs(LivingEntity caster, double x, double z, double minY, double maxY, float rotation, int delay) {
        BlockPos blockpos = new BlockPos((int) x, (int) maxY, (int) z);
        boolean flag = false;
        double d0 = 0.0D;
        do {
            BlockPos belowPos = blockpos.below();
            BlockState belowState = caster.level().getBlockState(belowPos);
            if (belowState.isFaceSturdy(caster.level(), belowPos, Direction.UP)) {
                if (!caster.level().isEmptyBlock(blockpos)) {
                    BlockState state = caster.level().getBlockState(blockpos);
                    VoxelShape voxelshape = state.getCollisionShape(caster.level(), blockpos);
                    if (!voxelshape.isEmpty()) {
                        d0 = voxelshape.max(Direction.Axis.Y);
                    }
                }
                flag = true;
                break;
            }
            blockpos = blockpos.below();
        } while (blockpos.getY() >= Mth.floor(minY) - 1);
        if (flag) {
            caster.level().addFreshEntity(new EvokerFangs(caster.level(), x, (double) blockpos.getY() + d0, z, rotation, delay, caster));
        }
    }
}
