package com.huskydreaming.authenticator.utilities;

import org.bukkit.configuration.file.FileConfiguration;

public enum Locale {
    AUTHENTICATED("&aYou have successfully been authenticated."),
    AUTHENTICATION_CODE("&aPlease type in the authentication code:"),
    AUTHENTICATION_CODE_INCORRECT("&7You must use the correct code from the authenticator app to verify."),
    AUTHENTICATED_NULL("&7You must be authenticated to do this action."),
    BACKUP_CODES_FORMAT("&b{0}. &7{1}"),
    BACKUP_CODES_HEADER("&a&lBackup Codes:"),
    BACKUP_CODES_RESET("&7Your backup codes have been reset. You can type &f/authenticator backupcodes &7to check them."),
    BACKUP_CODES_VERIFY("&7Backup codes have been generated for you. You can type &f/authenticator backupcodes &7to keep them safe."),
    NO_PERMISSION("&7You do not seem to have permissions for this command."),
    OFFLINE_PLAYER_INVALID("&7The player&f {0}&7 has never played before."),
    OFFLINE_PLAYER_REMOVED("&7You have removed&f {0}&7 from authentication."),
    OFFLINE_PLAYER_UNVERIFIED("&7The player &f{0} &7is not verified."),
    RELOAD("&7You have successfully reloaded all configurations"),
    SCAN_QR("&aScan the QR code in the authenticator application of your choice. Once complete..."),
    VERIFICATION_INCORRECT("&cYou must use the correct code to verify."),
    VERIFIED("&aYou have successfully been verified.");

    private final String def;
    private static FileConfiguration configuration;

    Locale(String def) {
        this.def = def;
    }

    public String parse() {
        return configuration.getString(toString(), def);
    }

    @Override
    public String toString() {
        return name().toLowerCase().replace("_", "-");
    }

    public static void setConfiguration(FileConfiguration configuration) {
        Locale.configuration = configuration;
    }
}
