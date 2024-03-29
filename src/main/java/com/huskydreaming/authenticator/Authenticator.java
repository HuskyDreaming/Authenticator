package com.huskydreaming.authenticator;

import com.huskydreaming.authenticator.authentication.AuthenticationCommand;
import com.huskydreaming.authenticator.authentication.AuthenticationHandler;
import com.huskydreaming.authenticator.authentication.AuthenticationListener;
import com.huskydreaming.authenticator.requests.RequestHandler;
import com.huskydreaming.authenticator.requests.RequestListener;
import com.huskydreaming.authenticator.utilities.Locale;
import com.huskydreaming.authenticator.utilities.Yaml;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public class Authenticator extends JavaPlugin {

    private Yaml locale;
    private AuthenticationHandler authenticationHandler;
    private RequestHandler requestHandler;

    private int backupCodesAmount;
    private int backupCodesLength;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        backupCodesLength = getConfig().getInt("backup-codes.length");
        backupCodesAmount = getConfig().getInt("backup-codes.amount");

        locale = new Yaml("locale");
        locale.load(this);
        Locale.setConfiguration(locale.getConfiguration());

        for (Locale message : Locale.values()) {
            locale.getConfiguration().set(message.toString(), message.parse());
        }
        locale.save();

        authenticationHandler = new AuthenticationHandler(this);
        requestHandler = new RequestHandler(this, authenticationHandler);

        for (Player player : getServer().getOnlinePlayers()) {
            if (player.hasPermission("authenticator.use") || player.isOp()) {
                requestHandler.sendRequest(player);
            }
        }

        registerListeners(
                new RequestListener(requestHandler),
                new AuthenticationListener(authenticationHandler)
        );

        PluginCommand pluginCommand = getCommand("authenticator");
        if (pluginCommand != null) pluginCommand.setExecutor(new AuthenticationCommand(this));
    }

    @Override
    public void onDisable() {
        getServer().getOnlinePlayers().forEach(player -> authenticationHandler.cleanup(player));
    }

    private void registerListeners(Listener... listeners) {
        PluginManager pluginManager = getServer().getPluginManager();
        Arrays.stream(listeners).forEach(listener -> pluginManager.registerEvents(listener, this));
    }

    public void reload() {
        reloadConfig();
        locale.reload(this);

        for (Player player : getServer().getOnlinePlayers()) {
            if (player.hasPermission("authenticator.use") || player.isOp()) {
                requestHandler.sendRequest(player);
            }
        }
    }

    @NotNull
    public AuthenticationHandler getAuthenticationHandler() {
        return authenticationHandler;
    }

    @NotNull
    public RequestHandler getRequestHandler() {
        return requestHandler;
    }

    public int getBackupCodesAmount() {
        return backupCodesAmount;
    }

    public int getBackupCodesLength() {
        return backupCodesLength;
    }
}
