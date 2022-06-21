package com.huskydreaming.authenticator.requests;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;

public class RequestListener implements Listener {

    private final RequestHandler requestHandler;

    public RequestListener(RequestHandler requestHandler) {
        this.requestHandler = requestHandler;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        if(player.hasPermission("authenticator.use") || player.isOp()) {
            requestHandler.sendRequest(player);
        }
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        if(requestHandler.hasRequest(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent event) {
        if(requestHandler.hasRequest(event.getPlayer())) {
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
