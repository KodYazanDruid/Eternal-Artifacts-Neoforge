package com.sonamorningstar.eternalartifacts.network;

import com.sonamorningstar.eternalartifacts.content.item.WitheringSword;
import com.sonamorningstar.eternalartifacts.network.base.RegisterPacket;
import com.sonamorningstar.eternalartifacts.util.RayTraceHelper;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;

import static com.sonamorningstar.eternalartifacts.EternalArtifacts.MODID;

@RegisterPacket(side = RegisterPacket.PacketSide.SERVER)
public record ShootSkullsToServer(ItemStack stack, InteractionHand hand) implements CustomPacketPayload {
    public static final ResourceLocation ID = new ResourceLocation(MODID, "shoot_skull");

    public static ShootSkullsToServer create(FriendlyByteBuf buff) { return new ShootSkullsToServer(buff.readItem(), buff.readEnum(InteractionHand.class)); }
    public static ShootSkullsToServer create(ItemStack stack, InteractionHand hand) { return new ShootSkullsToServer(stack, hand); }

    @Override
    public void write(FriendlyByteBuf buff) {
        buff.writeItem(stack);
        buff.writeEnum(hand);
    }

    @Override
    public ResourceLocation id() {return ID;}

    public void handle(PlayPayloadContext ctx) {
        ctx.workHandler().submitAsync(()-> ctx.player().ifPresent(player -> {
            if (player.level().isLoaded(player.blockPosition())) {
                BlockHitResult result = RayTraceHelper.retrace(player, ClipContext.Fluid.NONE);
                if (result.getType() == HitResult.Type.MISS || result.getType() == HitResult.Type.ENTITY){
                    Vec3 loc = result.getLocation();
                    WitheringSword.shootSkull(player.level(), player, loc.x, loc.y, loc.z, stack, hand);
                }
            }
        }));
    }
}
