package com.sonamorningstar.eternalartifacts.content.item;

import com.sonamorningstar.eternalartifacts.core.ModTiers;
import com.sonamorningstar.eternalartifacts.event.client.ClientEvents;
import com.sonamorningstar.eternalartifacts.util.RayTraceHelper;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.WitherSkull;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

public class WitheringSword extends SwordItem {
    public WitheringSword() {
        super(ModTiers.WITHER, 4, -2.4F, new Properties().rarity(Rarity.EPIC).stacksTo(1));
    }

    /**
     * Players are handled in {@link ClientEvents#leftClickEvent(PlayerInteractEvent.LeftClickEmpty)}
     * */
    @Override
    public boolean onEntitySwing(ItemStack stack, LivingEntity entity) {
        if (entity.level().isClientSide || entity instanceof Player) return super.onEntitySwing(stack, entity);
        BlockHitResult result = RayTraceHelper.retrace(entity, ClipContext.Fluid.NONE);
        if (result.getType() == HitResult.Type.MISS) {
            Vec3 location = result.getLocation();
            shootSkull(entity.level(), entity, location.x, location.y, location.z, stack, InteractionHand.MAIN_HAND);
        }
        return super.onEntitySwing(stack, entity);
    }

    public static void shootSkull(Level level, LivingEntity shooter, double x, double y, double z, ItemStack sword, InteractionHand hand) {
        level.levelEvent(null, 1024, shooter.getOnPos(), 0);
        double d0 = shooter.getX();
        double d1 = shooter.getEyeY();
        double d2 = shooter.getZ();
        double d3 = x - d0;
        double d4 = y - d1;
        double d5 = z - d2;
        WitherSkull skull = new WitherSkull(level, shooter, d3, d4, d5);
        skull.setPosRaw(d0, d1, d2);
        skull.setDeltaMovement(skull.getDeltaMovement().scale(100.0D));
        level.addFreshEntity(skull);
        sword.hurtAndBreak(1, shooter, owner -> owner.broadcastBreakEvent(hand));
        if (shooter instanceof Player player) {
            player.getCooldowns().addCooldown(sword.getItem(), 20);
        }
    }

}
