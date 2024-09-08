package com.sonamorningstar.eternalartifacts.content.recipe.container;

import com.google.common.collect.Lists;
import com.sonamorningstar.eternalartifacts.content.recipe.container.base.ItemlessContainer;
import net.minecraft.world.ContainerListener;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SimpleEntityContainer extends ItemlessContainer {
    private final int size;
    private final List<EntityType<?>> entityTypeList;
    @Nullable
    private List<ContainerListener> listeners;

    public SimpleEntityContainer(int size) {
        this.size = size;
        this.entityTypeList = new ArrayList<>(size);
    }

    public SimpleEntityContainer(EntityType<?>... entityTypes) {
        this.size = entityTypes.length;
        this.entityTypeList = Arrays.stream(entityTypes).toList();
    }

    public void addListener(ContainerListener listener) {
        if (this.listeners == null) this.listeners = Lists.newArrayList();
        this.listeners.add(listener);
    }

    public void removeListener(ContainerListener pListener) {
        if (this.listeners != null) this.listeners.remove(pListener);
    }

    private boolean isInBounds(int index) {
        return index < size && index >= 0;
    }

    public void setEntityType(EntityType<?> type, int index) {
        if (isInBounds(index)) {
            entityTypeList.set(index, type);
            setChanged();
        }
    }

    public EntityType<?> addEntityType(EntityType<?> type) {
       entityTypeList.add(type);
       setChanged();
       return type;
    }

    @Nullable
    public EntityType<?> getEntityType(int index) {
        return isInBounds(index) ? entityTypeList.get(index) : null;
    }

    @Override
    public int getContainerSize() {return size;}

    @Override
    public boolean isEmpty() {return entityTypeList.isEmpty();}

    @Override
    public boolean stillValid(Player pPlayer) { return true; }

    @Override
    public void setChanged() {
        if (this.listeners != null) {
            for(ContainerListener containerlistener : this.listeners) {
                containerlistener.containerChanged(this);
            }
        }
    }

    @Override
    public void clearContent() {
        this.entityTypeList.clear();
        this.setChanged();
    }
}
