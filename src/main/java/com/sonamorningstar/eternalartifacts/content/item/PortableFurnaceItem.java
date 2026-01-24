package com.sonamorningstar.eternalartifacts.content.item;

import com.sonamorningstar.eternalartifacts.content.item.base.EnergyConsumerItem;
import com.sonamorningstar.eternalartifacts.core.ModBlocks;
import com.sonamorningstar.eternalartifacts.event.custom.charms.CharmTickEvent;
import com.sonamorningstar.eternalartifacts.network.Channel;
import com.sonamorningstar.eternalartifacts.network.SyncCharmTagsToClient;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.IItemHandlerModifiable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.sonamorningstar.eternalartifacts.EternalArtifacts.MODID;

@Mod.EventBusSubscriber(modid = MODID)
public class PortableFurnaceItem extends EnergyConsumerItem {
	public PortableFurnaceItem(Properties props) {
		super(props);
	}
	
	@Override
	public boolean isBarVisible(ItemStack stack) {
		if (!stack.hasTag()) return false;
		CompoundTag tag = stack.getTag();
		return tag.getInt("Progress") > 0;
	}
	
	@Override
	public int getBarWidth(ItemStack stack) {
		if (!stack.hasTag()) return 0;
		CompoundTag tag = stack.getTag();
		int progress = tag.getInt("Progress");
		int maxProgress = tag.getInt("MaxProgress");
		if (maxProgress == 0) return 0;
		return Math.round((float) progress / maxProgress * 13.0F);
	}
	
	@Override
	public int getBarColor(ItemStack stack) {
		return 0xFF8000;
	}
	
	@SubscribeEvent
	private static void charmTick(CharmTickEvent event) {
		ItemStack charm = event.getCharm();
		LivingEntity living = event.getEntity();
		int charmSlot = event.getSlot();
		Level level = living.level();
		if (!level.isClientSide() && charm.getItem() instanceof PortableFurnaceItem) {
			Player player = living instanceof Player p ? p : null;
			IItemHandler itemHandler = charm.getCapability(Capabilities.ItemHandler.ITEM);
			int burnAmount = 2;
			if (itemHandler != null) {
				ItemStack input = itemHandler.getStackInSlot(0);
				SimpleContainer container = new SimpleContainer(input);
				Optional<RecipeHolder<SmeltingRecipe>> recipeOptional = level.getRecipeManager().getRecipeFor(RecipeType.SMELTING, container, level);
				if (recipeOptional.isPresent()) {
					RecipeHolder<SmeltingRecipe> recipeHolder = recipeOptional.get();
					SmeltingRecipe recipe = recipeHolder.value();
					ItemStack result = recipe.assemble(container, level.registryAccess());
					ItemStack remaining = itemHandler.insertItem(2, result, true);
					if (!remaining.isEmpty()) {
						resetFurnaceProgress(charm, charmSlot, player);
					} else {
						ItemStack fuel = itemHandler.getStackInSlot(1);
						if (itemHandler instanceof IItemHandlerModifiable modifiable && refuel(charm, fuel, charmSlot, player)) {
							if (fuel.hasCraftingRemainingItem()) {
								modifiable.setStackInSlot(1, fuel.getCraftingRemainingItem());
							} else if (!fuel.isEmpty()){
								itemHandler.extractItem(1, 1, false);
								if (fuel.isEmpty()) {
									modifiable.setStackInSlot(1, fuel.getCraftingRemainingItem());
								}
							}
						}
						if (hasFuel(charm)) {
							setMaxProgress(charm, recipe.getCookingTime(), charmSlot, player);
							progressFurnace(charm, charmSlot, player);
							burnAmount--;
						} else {
							resetFurnaceProgress(charm, charmSlot, player);
						}
						if (isRecipeComplete(charm, recipe)) {
							ItemStack fuelStack = itemHandler.getStackInSlot(1);
							if ((input.is(Items.WET_SPONGE) || input.is(ModBlocks.WET_INDUSTRIAL_SPONGE.asItem()))
										&& fuelStack.is(Items.BUCKET) && fuelStack.getCount() == 1) {
								itemHandler.extractItem(1, 1, false);
								itemHandler.insertItem(1, new ItemStack(Items.WATER_BUCKET), false);
							}
							itemHandler.extractItem(0, 1, false);
							itemHandler.insertItem(2, result, false);
							addRecipeUsed(charm, recipeHolder, charmSlot, player);
							resetFurnaceProgress(charm, charmSlot, player);
						}
					}
					
				} else {
					resetFurnaceProgress(charm, charmSlot, player);
				}
			}
			burnFuel(charm, charmSlot, player, burnAmount);
		}
	}
	
	public static void addRecipeUsed(ItemStack furnace, RecipeHolder<?> recipe, int slotId, Player player) {
		CompoundTag tag = furnace.getOrCreateTag();
		CompoundTag recipesUsed = tag.getCompound("RecipesUsed");
		String recipeId = recipe.id().toString();
		int count = recipesUsed.getInt(recipeId);
		recipesUsed.putInt(recipeId, count + 1);
		tag.put("RecipesUsed", recipesUsed);
		syncToClient(slotId, player, furnace);
	}
	
	public static void awardUsedRecipesAndPopExperience(ItemStack furnace, ServerPlayer player, int slotId) {
		ServerLevel level = player.serverLevel();
		RecipeManager recipeManager = level.getRecipeManager();
		Object2IntOpenHashMap<ResourceLocation> recipesUsed = getRecipesUsed(furnace);
		
		List<RecipeHolder<?>> awardedRecipes = new ArrayList<>();
		int totalXp = 0;
		
		for (var entry : recipesUsed.object2IntEntrySet()) {
			ResourceLocation recipeId = entry.getKey();
			int count = entry.getIntValue();
			
			var recipeOpt = recipeManager.byKey(recipeId);
			if (recipeOpt.isPresent()) {
				RecipeHolder<?> recipe = recipeOpt.get();
				awardedRecipes.add(recipe);
				
				if (recipe.value() instanceof SmeltingRecipe smeltingRecipe) {
					float experience = smeltingRecipe.getExperience();
					for (int i = 0; i < count; i++) {
						totalXp += Mth.floor(experience);
						float fractional = Mth.frac(experience);
						if (fractional != 0.0F && Math.random() < (double) fractional) {
							totalXp++;
						}
					}
				}
			}
		}
		
		if (totalXp > 0) {
			ExperienceOrb.award(level, player.position(), totalXp);
		}
		
		player.awardRecipes(awardedRecipes);
		
		furnace.getOrCreateTag().remove("RecipesUsed");
		syncToClient(slotId, player, furnace);
	}
	

	public static Object2IntOpenHashMap<ResourceLocation> getRecipesUsed(ItemStack furnace) {
		Object2IntOpenHashMap<ResourceLocation> map = new Object2IntOpenHashMap<>();
		if (furnace.hasTag()) {
			CompoundTag tag = furnace.getTag();
			CompoundTag recipesUsed = tag.getCompound("RecipesUsed");
			for (String key : recipesUsed.getAllKeys()) {
				map.put(new ResourceLocation(key), recipesUsed.getInt(key));
			}
		}
		return map;
	}
	
	private static void syncToClient(int slotId, Player player, ItemStack charm) {
		if (player instanceof ServerPlayer serverPlayer) {
			Channel.sendToPlayer(new SyncCharmTagsToClient(slotId, charm.getTag()), serverPlayer);
		}
	}
	
	public static void progressFurnace(ItemStack furnace, int slotId, Player player) {
		CompoundTag tag = furnace.getOrCreateTag();
		int progress = tag.getInt("Progress");
		progress++;
		tag.putInt("Progress", progress);
		syncToClient(slotId, player, furnace);
	}
	
	public static void setMaxProgress(ItemStack furnace, int maxProgress, int slotId, Player player) {
		CompoundTag tag = furnace.getOrCreateTag();
		tag.putInt("MaxProgress", maxProgress);
		syncToClient(slotId, player, furnace);
	}
	
	public static boolean isRecipeComplete(ItemStack furnace, SmeltingRecipe recipe) {
		if (!furnace.hasTag()) return false;
		CompoundTag tag = furnace.getTag();
		int progress = tag.getInt("Progress");
		return progress >= recipe.getCookingTime();
	}
	
	public static void resetFurnaceProgress(ItemStack furnace, int slotId, Player player) {
		CompoundTag tag = furnace.getOrCreateTag();
		tag.putInt("Progress", 0);
		syncToClient(slotId, player, furnace);
	}
	
	public static boolean burnFuel(ItemStack furnace, int slotId, Player player, int amount) {
		CompoundTag tag = furnace.getOrCreateTag();
		int fuel = tag.getInt("Fuel");
		if (fuel > 0) {
			fuel = Math.max(fuel - amount, 0);
			tag.putInt("Fuel", fuel);
			syncToClient(slotId, player, furnace);
			return true;
		}
		return false;
	}
	
	public static boolean hasFuel(ItemStack furnace) {
		if (!furnace.hasTag()) return false;
		CompoundTag tag = furnace.getTag();
		int fuel = tag.getInt("Fuel");
		return fuel > 0;
	}
	
	public static boolean refuel(ItemStack furnace, ItemStack fuel, int slotId, Player player) {
		int burnTime = fuel.getBurnTime(RecipeType.SMELTING);
		CompoundTag tag = furnace.getOrCreateTag();
		int currentFuel = tag.getInt("Fuel");
		if (burnTime > 0 && currentFuel == 0) {
			currentFuel = burnTime;
			tag.putInt("Fuel", currentFuel);
			tag.putInt("MaxFuel", burnTime);
			syncToClient(slotId, player, furnace);
			return true;
		}
		return false;
	}
	
	public static ContainerData getContainerData(ItemStack charm) {
		return new ContainerData() {
			@Override
			public int get(int index) {
				if (!charm.hasTag()) return 0;
				CompoundTag tag = charm.getTag();
				return switch (index) {
					case 0 -> tag.getInt("Fuel");
					case 1 -> tag.getInt("MaxFuel");
					case 2 -> tag.getInt("Progress");
					default -> tag.getInt("MaxProgress");
				};
			}
			
			@Override
			public void set(int index, int value) {
				switch (index) {
					case 0 -> {
						CompoundTag tag = charm.getOrCreateTag();
						tag.putInt("Fuel", value);
					}
					case 1 -> {
						CompoundTag tag = charm.getOrCreateTag();
						tag.putInt("MaxFuel", value);
					}
					case 2 -> {
						CompoundTag tag = charm.getOrCreateTag();
						tag.putInt("Progress", value);
					}
					case 3 -> {
						CompoundTag tag = charm.getOrCreateTag();
						tag.putInt("MaxProgress", value);
					}
				}
			}
			
			@Override
			public int getCount() {
				return 4;
			}
		};
	}
}
