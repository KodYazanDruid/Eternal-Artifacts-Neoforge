package com.sonamorningstar.eternalartifacts.api;

import com.mojang.authlib.GameProfile;
import com.sonamorningstar.eternalartifacts.content.block.entity.base.Machine;
import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.neoforged.neoforge.common.util.FakePlayer;
import org.jetbrains.annotations.Nullable;

import java.util.OptionalInt;
import java.util.function.Consumer;

@Getter
public class ModFakePlayer extends FakePlayer {
	private final Machine<?> machine;
	public ModFakePlayer(ServerLevel level, GameProfile name, Machine<?> machine) {
		super(level, name);
		this.machine = machine;
	}
	
	@Override
	public void initMenu(AbstractContainerMenu pMenu) {
	}
	
	@Override
	public OptionalInt openMenu(@Nullable MenuProvider pMenu) {
		return OptionalInt.empty();
	}
	
	@Override
	public OptionalInt openMenu(MenuProvider menuProvider, BlockPos pos) {
		return OptionalInt.empty();
	}
	
	@Override
	public OptionalInt openMenu(@Nullable MenuProvider pMenu, @Nullable Consumer<FriendlyByteBuf> extraDataWriter) {
		return OptionalInt.empty();
	}
}
