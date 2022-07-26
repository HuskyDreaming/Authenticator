package com.huskydreaming.authenticator.authentication;

import org.bukkit.NamespacedKey;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class AuthenticationListener implements Listener {

    private final AuthenticationHandler authenticationHandler;

    public AuthenticationListener(AuthenticationHandler authenticationHandler) {
        this.authenticationHandler = authenticationHandler;
    }

    @EventHandler
    public void onItemPickup(EntityPickupItemEvent event) {
        if(isAuthenticatorItem(event.getItem())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onItemDrop(PlayerDropItemEvent event) {
        if (isAuthenticatorItem(event.getItemDrop())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        authenticationHandler.cleanup(event.getPlayer());
    }

    private boolean isAuthenticatorItem(Item item) {
        ItemStack itemStack = item.getItemStack();
        ItemMeta itemMeta = itemStack.getItemMeta();

        if (itemMeta == null) return false;

        NamespacedKey namespacedKey = authenticationHandler.getNamespacedKey();
        PersistentDataContainer persistentDataContainer = itemMeta.getPersistentDataContainer();
        return persistentDataContainer.has(namespacedKey, PersistentDataType.STRING);
    }
}
