package com.huskydreaming.authenticator.authentication;

import com.huskydreaming.authenticator.Authenticator;
import com.huskydreaming.authenticator.requests.RequestHandler;
import com.huskydreaming.authenticator.utilities.Chat;
import com.huskydreaming.authenticator.utilities.Locale;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class AuthenticationCommand implements CommandExecutor, TabCompleter {

    private final Authenticator authenticator;

    public AuthenticationCommand(Authenticator authenticator) {
        this.authenticator = authenticator;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String label, @NotNull String[] strings) {
        if (strings.length == 1) {
            if (strings[0].equalsIgnoreCase("backupcodes") || commandSender.isOp()) {
                if (!commandSender.hasPermission("authenticator.backupcodes")) {
                    commandSender.sendMessage(Chat.parameterize(Locale.NO_PERMISSION));
                    return true;
                }

                Authentication authentication = authenticator.getAuthenticationHandler().getAuthentication((Player) commandSender);
                if (authentication == null) {
                    commandSender.sendMessage(Chat.parameterize(Locale.AUTHENTICATED_NULL));
                } else {
                    commandSender.sendMessage(authentication.getBackupCodes());
                }
                return true;
            }
            if (strings[0].equalsIgnoreCase("reload") || commandSender.isOp()) {
                if (!commandSender.hasPermission("authenticator.reload")) {
                    commandSender.sendMessage(Chat.parameterize(Locale.NO_PERMISSION));
                    return true;
                }

                authenticator.reload();
                commandSender.sendMessage(Chat.parameterize(Locale.RELOAD));
                return true;
            }
            if (strings[0].equalsIgnoreCase("resetcodes") || commandSender.isOp()) {
                if (!commandSender.hasPermission("authenticator.backupcodes")) {
                    commandSender.sendMessage(Chat.parameterize(Locale.NO_PERMISSION));
                    return true;
                }
                Authentication authentication = authenticator.getAuthenticationHandler().getAuthentication((Player) commandSender);
                if (authentication == null) {
                    commandSender.sendMessage(Chat.parameterize(Locale.AUTHENTICATED_NULL));
                } else {
                    authentication.createCodes(authenticator.getBackupCodesLength(), authenticator.getBackupCodesAmount());
                    commandSender.sendMessage(Chat.parameterize(Locale.BACKUP_CODES_RESET));
                }
                return true;
            }
        } else if (strings.length == 2) {
            if (strings[0].equalsIgnoreCase("remove") || commandSender.isOp()) {
                if (!commandSender.hasPermission("authenticator.remove")) {
                    commandSender.sendMessage(Chat.parameterize(Locale.NO_PERMISSION));
                    return true;
                }
                OfflinePlayer offlinePlayer = getOfflinePlayer(strings[1]);
                if (offlinePlayer == null) {
                    commandSender.sendMessage(Chat.parameterize(Locale.OFFLINE_PLAYER_INVALID, strings[1]));
                    return true;
                }

                AuthenticationHandler authenticationHandler = authenticator.getAuthenticationHandler();
                if (authenticationHandler.isVerified(offlinePlayer)) {
                    commandSender.sendMessage(Chat.parameterize(Locale.OFFLINE_PLAYER_REMOVED, strings[1]));
                    authenticationHandler.remove(offlinePlayer);

                    Player player = offlinePlayer.getPlayer();
                    RequestHandler requestHandler = authenticator.getRequestHandler();
                    if (player != null) requestHandler.removeRequest(player);
                } else {
                    commandSender.sendMessage(Chat.parameterize(Locale.OFFLINE_PLAYER_UNVERIFIED, strings[1]));
                }
            }
        }
        return false;
    }

    private OfflinePlayer getOfflinePlayer(String name) {
        for (OfflinePlayer offlinePlayer : Bukkit.getOfflinePlayers()) {
            String offlinePlayerName = offlinePlayer.getName();
            if (offlinePlayerName != null && offlinePlayerName.equalsIgnoreCase(name)) {
                return offlinePlayer;
            }
        }
        return null;
    }

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] strings) {
        List<String> list = new ArrayList<>();
        if (strings.length == 1) {
            if (sender.hasPermission("authenticator.backupcodes") || sender.isOp()) list.add("backupcodes");
            if (sender.hasPermission("authenticator.resetcodes") || sender.isOp()) list.add("resetcodes");
            if (sender.hasPermission("authenticator.remove") || sender.isOp()) list.add("remove");
            if (sender.hasPermission("authenticator.reload") || sender.isOp()) list.add("reload");
        }
        if (strings.length == 2 && strings[0].equalsIgnoreCase("remove")) {
            if (sender.hasPermission("authenticator.remove") || sender.isOp()) {
                Bukkit.getOnlinePlayers().forEach(player -> list.add(player.getName()));
            }
        }
        return list;
    }
}
