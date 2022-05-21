package com.huskydreaming.authenticator.requests;

import com.huskydreaming.authenticator.authentication.Authentication;
import com.huskydreaming.authenticator.authentication.AuthenticationHandler;
import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class RequestHandler {

    private final AuthenticationHandler authenticationHandler;
    private final Map<UUID, Request> requests = new ConcurrentHashMap<>();

    public RequestHandler(AuthenticationHandler authenticationHandler) {
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
            player.sendMessage(ChatColor.GREEN + "Scan the QR code in the authenticator application of your choice. Once complete...");
        }

        player.sendMessage(ChatColor.GREEN + "Please type in the authentication code:");

        requests.put(player.getUniqueId(), authenticationRequest);
    }

    public void processRequest(Player player, String code) {
        Request authenticationRequest = requests.get(player.getUniqueId());
        if (authenticationRequest != null) {
            switch (authenticationRequest.getAuthenticationType()) {
                case VERIFY: {
                    Authentication authentication = authenticationRequest.getAuthentication();
                    if (authenticationHandler.isVerified(authentication, code)) {
                        authenticationHandler.verify(player, authentication);
                        authenticationHandler.cleanup(player);

                        player.sendMessage("You have successfully been verified.");
                        requests.remove(player.getUniqueId());
                    } else {
                        player.sendMessage("You must use the correct code from the authenticator app to verify.");
                    }
                    break;
                }
                case AUTHENTICATE: {
                    Authentication authentication = authenticationHandler.getAuthentications().get(player.getUniqueId());
                    if (authenticationHandler.isVerified(authentication, code)) {
                        player.sendMessage("You have successfully been authenticated.");
                        requests.remove(player.getUniqueId());
                    } else {
                        player.kickPlayer("You must use the correct code to authenticate.");
                    }
                    break;
                }
            }

        }
    }
}
