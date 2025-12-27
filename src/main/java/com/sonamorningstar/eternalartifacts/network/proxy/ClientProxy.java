package com.sonamorningstar.eternalartifacts.network.proxy;

import com.mojang.authlib.GameProfile;
import com.mojang.datafixers.util.Either;
import com.sonamorningstar.eternalartifacts.api.machine.tesseract.TesseractNetwork;
import com.sonamorningstar.eternalartifacts.api.machine.tesseract.TesseractNetworks;
import com.sonamorningstar.eternalartifacts.capabilities.energy.ModEnergyStorage;
import com.sonamorningstar.eternalartifacts.api.charm.CharmStorage;
import com.sonamorningstar.eternalartifacts.client.gui.TabHandler;
import com.sonamorningstar.eternalartifacts.client.gui.screen.EnderNotebookScreen;
import com.sonamorningstar.eternalartifacts.client.gui.screen.EntityCatalogueScreen;
import com.sonamorningstar.eternalartifacts.client.gui.screen.LightSaberScreen;
import com.sonamorningstar.eternalartifacts.client.gui.screen.TesseractScreen;
import com.sonamorningstar.eternalartifacts.client.gui.widget.ScrollablePanel;
import com.sonamorningstar.eternalartifacts.client.gui.widget.SimpleDraggablePanel;
import com.sonamorningstar.eternalartifacts.client.gui.widget.TesseractWhitelistComponentWidget;
import com.sonamorningstar.eternalartifacts.container.slot.BlueprintFakeSlot;
import com.sonamorningstar.eternalartifacts.content.item.EnderNotebookItem;
import com.sonamorningstar.eternalartifacts.content.item.EntityCatalogueItem;
import com.sonamorningstar.eternalartifacts.content.item.LightSaberItem;
import com.sonamorningstar.eternalartifacts.core.ModDataAttachments;
import com.sonamorningstar.eternalartifacts.network.BlueprintIngredientsToClient;
import com.sonamorningstar.eternalartifacts.network.tesseract.RebuildTesseractPanelToClient;
import com.sonamorningstar.eternalartifacts.network.UpdateEntityEnergyToClient;
import com.sonamorningstar.eternalartifacts.network.charm.CycleWildcardToClient;
import com.sonamorningstar.eternalartifacts.network.charm.UpdateCharmsToClient;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class ClientProxy {
    static Minecraft mc = Minecraft.getInstance();

    static final Map<Predicate<ItemStack>, Consumer<ItemStack>> itemScreens = new HashMap<>();

    private static <I extends Item> void addItemScreen(Consumer<ItemStack> screenConsumer, Class<I> clazz) {
        itemScreens.put(stack-> clazz.isInstance(stack.getItem()), screenConsumer);
    }

    static {
        addItemScreen(ClientProxy::openEnderNotebook, EnderNotebookItem.class);
        addItemScreen(ClientProxy::openLightSaber, LightSaberItem.class);
        addItemScreen(ClientProxy::openEntityCatalogue, EntityCatalogueItem.class);
    }

    public static void requestScreen(ItemStack stack) {
        itemScreens.entrySet().stream()
            .filter(entry-> entry.getKey().test(stack))
            .findFirst()
            .ifPresent(entry-> entry.getValue().accept(stack));
    }

    public static void openEnderNotebook(ItemStack notebook) {
        openScreen(new EnderNotebookScreen(notebook));
    }
    public static void openLightSaber(ItemStack lightSaber) {
        openScreen(new LightSaberScreen(lightSaber));
    }
    public static void openEntityCatalogue(ItemStack stack) {
        openScreen(new EntityCatalogueScreen(stack));
    }

    public static void openScreen(Screen screen) {
        mc.setScreen(screen);
    }

    @Nullable
    public static Entity getPlayerFromId(int id) {
        ClientLevel level = mc.level;
        return level != null ? level.getEntity(id) : null;
    }

    public static void handleUpdateCharms(UpdateCharmsToClient packet, PlayPayloadContext ctx) {
        mc.executeBlocking(()-> {
            Entity entity = mc.level.getEntity(packet.entityId());
            if (entity instanceof LivingEntity living){
                CharmStorage charms = new CharmStorage(living);
                for (int i = 0; i < packet.items().size(); i++)
                    charms.setStackInSlot(i, packet.items().get(i));
                living.setData(ModDataAttachments.CHARMS, charms);
                TabHandler tabs = TabHandler.INSTANCE;
                if (tabs != null) tabs.reloadTabs();
                charms.invalidateMorph();
            }
        });
    }

    public static void handleUpdateSheepEnergy(UpdateEntityEnergyToClient packet, PlayPayloadContext ctx) {
        mc.executeBlocking(()-> ctx.player().ifPresent(player ->{
            Level level = player.level();
            Entity entity = level.getEntity(packet.entityId());
            if (entity != null) {
                IEnergyStorage energyStorage = entity.getCapability(Capabilities.EnergyStorage.ENTITY, null);
                if (energyStorage instanceof ModEnergyStorage mes) {
                    mes.setEnergy(packet.energy());
                }
            }
        }));
    }
    
    public static void handleCycleWildcard(CycleWildcardToClient packet, PlayPayloadContext ctx) {
        mc.executeBlocking(()-> {
            Entity entity = mc.level.getEntity(packet.entityId());
            if (entity instanceof LivingEntity living) {
                var charms = CharmStorage.get(living);
                charms.setWildcardNbt(packet.value());
            }
        });
    }
    
    public static void setJumpTokens(int entityId, int jumps) {
        Entity entity = getPlayerFromId(entityId);
        if (entity instanceof Player player) {
            player.getPersistentData().putInt("JumpTokens", jumps);
        }
    }
    
    public static void rebuildTesseractPanel(RebuildTesseractPanelToClient pkt) {
        if (mc.screen instanceof TesseractScreen screen) {
            if (pkt.clearSelected()) {
                if (screen.getSelectedNetwork() != null) {
                    screen.getMenu().tesseract.setNetworkId(null);
                    screen.removeWidget(screen.getSelectedNetwork());
                    screen.rebuildWidgets();
                }
            } else {
                screen.rebuildWidgets();
            }
        }
    }
    
    public static void handleBlueprintIngredientsToClient(BlueprintIngredientsToClient packet) {
        Player player = mc.player;
        if (player == null) return;
        if (player.containerMenu.containerId == packet.containerId() && packet.containerId() != 0) {
            player.containerMenu.slots.stream().filter(s -> s instanceof BlueprintFakeSlot).forEach(s -> {
                BlueprintFakeSlot fakeSlot = (BlueprintFakeSlot) s;
                if (fakeSlot.getSlotIndex() < packet.ingredients().size()) {
                    fakeSlot.ingredient = packet.ingredients().get(fakeSlot.getSlotIndex());
                }
            });
        }
    }
    
    public static void onTesseractNetworksUpdated(TesseractNetworks clientInstance) {
        var tesseractNetworks = clientInstance.getTesseractNetworks();
        var font = mc.font;
        for (int i = 0; i < tesseractNetworks.size(); i++) {
            TesseractNetwork<?> tesseractNetwork = clientInstance.getTesseractNetworks().stream().toList().get(i);
            if(mc.screen instanceof TesseractScreen tesseractScreen && tesseractNetwork.getAccess() == TesseractNetwork.Access.PROTECTED) {
				tesseractScreen.children().stream()
					.filter(w -> w instanceof SimpleDraggablePanel panel && isCorrectPanel(tesseractNetwork, panel))
                    .map(w -> (SimpleDraggablePanel) w)
                    .forEach(whitelistPanel -> {
                        whitelistPanel.getChildren().stream()
                            .filter(w -> w instanceof ScrollablePanel)
                            .findFirst()
                            .map(w -> (ScrollablePanel<TesseractWhitelistComponentWidget>) w)
                            /*.map(w -> w instanceof ScrollablePanel<?> scrollablePanel ? scrollablePanel : null)
                            .filter(Objects::nonNull)
                            .findFirst()*/
                            .ifPresent(panel -> {
                                panel.getChildren().clear();
                                TesseractScreen.fillWhitelistPanel(tesseractNetwork, panel, font);
                            });
                    });
            }
        }
    }
	
	private static boolean isCorrectPanel(TesseractNetwork<?> network, SimpleDraggablePanel panel) {
		return panel.getId() != null && panel.getId().equals(TesseractScreen.WHITELIST_ID_PREFIX + "_" + network.getUuid());
	}
}