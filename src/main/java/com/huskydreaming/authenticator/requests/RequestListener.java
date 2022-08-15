package com.huskydreaming.authenticator.requests;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByBlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.player.*;

public class RequestListener implements Listener {

    private final RequestHandler requestHandler;

    public RequestListener(RequestHandler requestHandler) {
        this.requestHandler = requestHandler;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        if (player.hasPermission("authenticator.use") || player.isOp()) {
            if(requestHandler.hasRequest(player)) requestHandler.removeRequest(player);
            requestHandler.sendRequest(player);
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        requestHandler.removeRequest(event.getPlayer());
    }

    @EventHandler
    public void onDamageEvent(EntityDamageByEntityEvent event) {
        if(event.getEntity() instanceof Player player) {
            if (requestHandler.hasRequest(player)) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onDamageEvent(EntityDamageEvent event) {
        if(event.getEntity() instanceof Player player) {
            if (requestHandler.hasRequest(player)) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onDamageEvent(EntityDamageByBlockEvent event) {
        if(event.getEntity() instanceof Player player) {
            if (requestHandler.hasRequest(player)) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onItemPickup(EntityPickupItemEvent event) {
        if (event.getEntity() instanceof Player player) {
            if (requestHandler.hasRequest(player)) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onItemDrop(PlayerDropItemEvent event) {
        if (requestHandler.hasRequest(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        if (requestHandler.hasRequest(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent event) {
        if (requestHandler.hasRequest(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        if (requestHandler.hasRequest(event.getPlayer())) {
            requestHandler.processRequest(event.getPlayer(), event.getMessage());
            event.setCancelled(true);
        }
    }
}
