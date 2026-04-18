package com.sonamorningstar.eternalartifacts.api.item.armorset.sets.base;

import lombok.Getter;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.function.Predicate;

@Getter
public class ArmorSet {
	private final ResourceLocation key;
	private final List<Item> armorPieces;
	private final Predicate<ItemStack>[] armorPieceTesters;
	public boolean hasDescription = true;
	
	@SafeVarargs
	public ArmorSet(ResourceLocation key, List<Item> armorPieces, Predicate<ItemStack>... armorPieceTesters) {
		this.key = key;
		this.armorPieces = armorPieces;
		this.armorPieceTesters = armorPieceTesters;
	}
	
	public boolean is(ResourceLocation key) {
		return this.key.equals(key);
	}
	
	public boolean canActivate(List<ItemStack> equippedArmor) {
		if (equippedArmor.isEmpty()) return false;
		Queue<ItemStack> armorPiecesToCheck = new LinkedList<>(equippedArmor);
		while (!armorPiecesToCheck.isEmpty()) {
			ItemStack piece = armorPiecesToCheck.poll();
			boolean matchesPiece = false;
			boolean isPiece = isPiece(piece);
			if (isPiece) {
				matchesPiece = true;
			}
			if (!matchesPiece) {
				return false;
			}
		}
		return true;
	}
	
	public boolean isPiece(ItemStack stack) {
		if (armorPieceTesters.length == 0) {
			return armorPieces.contains(stack.getItem());
		} else {
			for (Predicate<ItemStack> tester : armorPieceTesters) {
				if (tester.test(stack)) {
					return true;
				}
			}
			return false;
		}
	}
	
	public boolean hasDescription() {
		return hasDescription;
	}
}