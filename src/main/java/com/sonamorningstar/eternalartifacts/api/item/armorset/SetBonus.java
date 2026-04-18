package com.sonamorningstar.eternalartifacts.api.item.armorset;

import com.sonamorningstar.eternalartifacts.core.ModDataAttachments;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import net.neoforged.neoforge.common.util.INBTSerializable;

import javax.annotation.Nullable;

@Getter
@Setter
public class SetBonus implements INBTSerializable<CompoundTag> {
	private final LivingEntity owner;
	@Nullable
	private ArmorSetRegistry.ArmorSetBonus activeBonus;
	
	public SetBonus(IAttachmentHolder holder) {
		this.owner = (LivingEntity) holder;
	}
	
	public static SetBonus get(LivingEntity entity) {
		return entity.getData(ModDataAttachments.SET_BONUS.get());
	}
	
	@Override
	public CompoundTag serializeNBT() {
		CompoundTag tag = new CompoundTag();
		if (activeBonus != null) {
			tag.putString("ActiveBonus", activeBonus.armorSet().getKey().toString());
		}
		return tag;
	}
	
	@Override
	public void deserializeNBT(CompoundTag tag) {
		if (tag.contains("ActiveBonus")) {
			activeBonus = ArmorSetRegistry.getBonus(ResourceLocation.tryParse(tag.getString("ActiveBonus")));
		} else {
			activeBonus = null;
		}
	}
}
