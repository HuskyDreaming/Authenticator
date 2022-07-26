package com.huskydreaming.authenticator.requests;

import com.huskydreaming.authenticator.authentication.Authentication;
import com.huskydreaming.authenticator.authentication.AuthenticationHandler;
import com.huskydreaming.authenticator.utilities.Chat;
import com.huskydreaming.authenticator.utilities.Locale;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class RequestHandler {

    private final Plugin plugin;
    private final AuthenticationHandler authenticationHandler;
    private final Map<UUID, Request> requests = new ConcurrentHashMap<>();

    public RequestHandler(Plugin plugin, AuthenticationHandler authenticationHandler) {
        this.plugin = plugin;
        this.authenticationHandler = authenticationHandler;
    }

    public boolean hasRequest(Player player) {
        return requests.containsKey(player.getUniqueId());
    }

    public void sendRequest(Player player) {
        Authentication authentication = authenticationHandler.getAuthentications().get(player.getUniqueId());
        Request authenticationRequest;
        if (authentication != null) {
            authenticationRequest = new Request(RequestType.AUTHENTICATE, authentication);
        } else {
            authenticationRequest = Request.create(RequestType.VERIFY);

            NamespacedKey namespacedKey = authenticationHandler.getNamespacedKey();
            ItemStack map = authenticationRequest.getAuthentication().createMap(namespacedKey, player);
            player.getInventory().addItem(map);
            player.sendMessage(Chat.parameterize(Locale.SCAN_QR));
        }

        correction(player);
        player.sendMessage(Chat.parameterize(Locale.AUTHENTICATION_CODE));
        requests.put(player.getUniqueId(), authenticationRequest);
    }

    public void processRequest(Player player, String code) {
        Request authenticationRequest = requests.get(player.getUniqueId());
        if (authenticationRequest != null) {
            switch (authenticationRequest.getAuthenticationType()) {
                case VERIFY -> {
                    Authentication authentication = authenticationRequest.getAuthentication();
                    if (authenticationHandler.isVerified(authentication, code)) {
                        authenticationHandler.verify(player, authentication);
                        Bukkit.getScheduler().runTask(plugin, () -> authenticationHandler.runCommands(player, plugin.getConfig()));
                        authenticationHandler.cleanup(player);

                        player.sendMessage(Chat.parameterize(Locale.VERIFIED));
                        requests.remove(player.getUniqueId());
                    } else {
                        player.sendMessage(Chat.parameterize(Locale.AUTHENTICATION_CODE_INCORRECT));
                    }
                }
                case AUTHENTICATE -> {
                    Authentication authentication = authenticationHandler.getAuthentications().get(player.getUniqueId());
                    if (authenticationHandler.isVerified(authentication, code)) {
                        Bukkit.getScheduler().runTask(plugin, () -> authenticationHandler.runCommands(player, plugin.getConfig()));
                        player.sendMessage(Chat.parameterize(Locale.AUTHENTICATED));
                        requests.remove(player.getUniqueId());
                    } else {
                        Bukkit.getScheduler().runTask(plugin, () -> player.kickPlayer(Chat.parameterize(Locale.VERIFICATION_INCORRECT)));
                    }
                }
            }

        }
    }

    public void removeRequest(Player player) {
        requests.remove(player.getUniqueId());
    }

    private void correction(Player player) {
        // Correction to see the map properly with QR code
        Location location = player.getLocation();
        Block block = player.getWorld().getHighestBlockAt(location.getBlockX(), location.getBlockZ());

        location = block.getLocation();
        location.setPitch(40.0f);
        location.add(0.5, 1, 0.5);
        player.teleport(location);
    }
}
