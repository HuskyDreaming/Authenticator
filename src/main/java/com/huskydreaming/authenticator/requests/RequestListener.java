package com.huskydreaming.authenticator.requests;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;

public class RequestListener implements Listener {

    private final RequestHandler authenticationRequestHandler;

    public RequestListener(RequestHandler authenticationRequestHandler) {
        this.authenticationRequestHandler = authenticationRequestHandler;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        if(player.hasPermission("authenticator.use") || player.isOp()) {

            // Correction to see the map properly with QR code
            Location location = player.getLocation();
            location.setPitch(40.0f);
            player.teleport(location);

            authenticationRequestHandler.sendRequest(event.getPlayer());
        }
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        if(authenticationRequestHandler.hasRequest(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent event) {
        if(authenticationRequestHandler.hasRequest(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        if (authenticationRequestHandler.hasRequest(event.getPlayer())) {
            authenticationRequestHandler.processRequest(event.getPlayer(), event.getMessage());
            event.setCancelled(true);
        }
    }
}
