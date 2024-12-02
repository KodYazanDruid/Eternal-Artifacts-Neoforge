package com.sonamorningstar.eternalartifacts.network.proxy;

import com.sonamorningstar.eternalartifacts.capabilities.energy.ModEnergyStorage;
import com.sonamorningstar.eternalartifacts.capabilities.item.CharmStorage;
import com.sonamorningstar.eternalartifacts.client.gui.TabHandler;
import com.sonamorningstar.eternalartifacts.client.gui.screen.EnderNotebookScreen;
import com.sonamorningstar.eternalartifacts.client.gui.screen.LightSaberScreen;
import com.sonamorningstar.eternalartifacts.client.gui.screen.base.ItemStackScreen;
import com.sonamorningstar.eternalartifacts.content.item.EnderNotebookItem;
import com.sonamorningstar.eternalartifacts.content.item.LightSaberItem;
import com.sonamorningstar.eternalartifacts.core.ModDataAttachments;
import com.sonamorningstar.eternalartifacts.network.UpdateEntityEnergyToClient;
import com.sonamorningstar.eternalartifacts.network.charm.UpdateCharmsToClient;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.Entity;
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
            Entity entity = mc.level.getEntity(packet.playerId());
            if (entity instanceof Player pl){
                CharmStorage charms = new CharmStorage(pl);
                for (int i = 0; i < packet.items().size(); i++)
                    charms.setStackInSlot(i, packet.items().get(i));
                pl.setData(ModDataAttachments.CHARMS, charms);
                TabHandler tabs = TabHandler.INSTANCE;
                if (tabs != null) tabs.reloadTabs();
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
}
