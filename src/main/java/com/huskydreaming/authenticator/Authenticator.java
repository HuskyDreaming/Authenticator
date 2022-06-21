package com.huskydreaming.authenticator;

import com.huskydreaming.authenticator.authentication.AuthenticationHandler;
import com.huskydreaming.authenticator.authentication.AuthenticationListener;
import com.huskydreaming.authenticator.authentication.AuthenticationCommand;
import com.huskydreaming.authenticator.requests.RequestHandler;
import com.huskydreaming.authenticator.requests.RequestListener;
import com.huskydreaming.authenticator.utilities.Locale;
import com.huskydreaming.authenticator.utilities.Yaml;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;

public class Authenticator extends JavaPlugin {

    private AuthenticationHandler authenticationHandler;

    @Override
    public void onEnable() {
        registerLocale(new Yaml("locale"));

        authenticationHandler = new AuthenticationHandler(this);
        authenticationHandler.deserialize();

        RequestHandler requestHandler = new RequestHandler(this, authenticationHandler);

        for(Player player : getServer().getOnlinePlayers()) {
            if(player.hasPermission("authenticator.use") || player.isOp()) {
                requestHandler.sendRequest(player);
            }
        }

        registerListeners(
                new RequestListener(requestHandler),
                new AuthenticationListener(authenticationHandler)
        );

        PluginCommand pluginCommand = getCommand("authenticator");
        if(pluginCommand != null) pluginCommand.setExecutor(new AuthenticationCommand(authenticationHandler, requestHandler));
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

    private void registerLocale(Yaml yaml) {
        yaml.reload(this);
        Locale.setConfiguration(yaml.getConfiguration());

        for (Locale message : Locale.values()) {
            yaml.getConfiguration().set(message.toString(), message.parse());
        }
        yaml.save();
    }

}
