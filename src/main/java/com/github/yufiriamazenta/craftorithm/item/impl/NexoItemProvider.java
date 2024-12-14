package com.github.yufiriamazenta.craftorithm.item.impl;

import com.github.yufiriamazenta.craftorithm.item.ItemProvider;
import com.nexomc.nexo.api.NexoItems;
import com.nexomc.nexo.items.ItemUpdater;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public enum NexoItemProvider implements ItemProvider {

    INSTANCE;

    @Override
    public @NotNull String namespace() {
        return "nexo";
    }

    @Override
    public @Nullable String getItemName(ItemStack itemStack, boolean ignoreAmount) {
        if (!NexoItems.exists(itemStack))
            return null;
        String itemName = NexoItems.idFromItem(itemStack);
        if (ignoreAmount) {
            return itemName;
        } else {
            ItemStack nexoItem = NexoItems.itemFromId(itemName).build();
            return itemName + " " + (itemStack.getAmount() / nexoItem.getAmount());
        }
    }

    @Override
    public @Nullable ItemStack getItem(String itemName) {
        if (!NexoItems.exists(itemName)) {
            return null;
        }
        ItemStack built = NexoItems.itemFromId(itemName).build();
        return ItemUpdater.updateItem(ItemStack#clone)
    }

    @Override
    public @Nullable ItemStack getItem(String itemName, OfflinePlayer player) {
        return getItem(itemName);
    }

}
