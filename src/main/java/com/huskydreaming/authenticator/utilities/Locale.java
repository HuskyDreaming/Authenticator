package com.huskydreaming.authenticator.utilities;

import org.bukkit.configuration.file.FileConfiguration;

public enum Locale {
    AUTHENTICATED("&aYou have successfully been authenticated."),
    AUTHENTICATION_CODE("&aPlease type in the authentication code:"),
    AUTHENTICATION_CODE_INCORRECT("&7You must use the correct code from the authenticator app to verify."),
    FEATURE_NOT_IMPLEMENTED("&7This feature has not been implemented."),
    NO_PERMISSION("&7You do not seem to have permissions for this command."),
    OFFLINE_PLAYER_INVALID("&7The player&f {0}&7 has never played before."),
    OFFLINE_PLAYER_REMOVED("&7You have removed&f {0}&7 from authentication."),
    OFFLINE_PLAYER_UNVERIFIED("&7The player &f{0} &7is not verified."),
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
