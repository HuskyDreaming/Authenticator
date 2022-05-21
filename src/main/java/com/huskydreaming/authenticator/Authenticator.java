package com.huskydreaming.authenticator;

import com.huskydreaming.authenticator.authentication.AuthenticationHandler;
import com.huskydreaming.authenticator.authentication.AuthenticationListener;
import com.huskydreaming.authenticator.requests.RequestHandler;
import com.huskydreaming.authenticator.requests.RequestListener;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;

public class Authenticator extends JavaPlugin {

    private AuthenticationHandler authenticationHandler;

    @Override
    public void onEnable() {
        authenticationHandler = new AuthenticationHandler(this);
        authenticationHandler.deserialize();

        registerListeners(
                new RequestListener(new RequestHandler(authenticationHandler)),
                new AuthenticationListener(authenticationHandler)
        );
    }

    @Override
    public void onDisable() {
        getServer().getOnlinePlayers().forEach(player -> authenticationHandler.cleanup(player));
        authenticationHandler.serialize();
    }

    private void registerListeners(Listener... listeners) {
        PluginManager pluginManager = getServer().getPluginManager();
        Arrays.stream(listeners).forEach(listener -> pluginManager.registerEvents(listener, this));
    }
}
