package com.huskydreaming.authenticator.authentication;

import com.huskydreaming.authenticator.code.CodeGenerator;
import com.huskydreaming.authenticator.qr.QrData;
import com.huskydreaming.authenticator.qr.QrGenerator;
import com.huskydreaming.authenticator.qr.QrRenderer;
import com.huskydreaming.authenticator.utilities.Chat;
import com.huskydreaming.authenticator.utilities.Locale;
import org.apache.commons.codec.binary.Base32;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.MapMeta;
import org.bukkit.map.MapView;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Authentication {

    private final String secret;
    private final Map<String, Boolean> backupCodes = new HashMap<>();

    public static Authentication create() {
        return new Authentication();
    }

    public Authentication() {
        SecureRandom secureRandom = new SecureRandom();
        Base32 base32 = new Base32();

        byte[] bytes = new byte[(32 * 5) / 8];
        secureRandom.nextBytes(bytes);

        secret = new String(base32.encode(bytes));
    }

    public ItemStack createMap(NamespacedKey namespacedKey, Player player) {
        QrData qrData = new QrData("Minecraft Server", player.getUniqueId().toString(), secret);
        QrGenerator qrGenerator = new QrGenerator();
        QrRenderer qrRenderer = new QrRenderer(qrGenerator.generate(qrData));

        MapView mapView = Bukkit.createMap(player.getWorld());
        mapView.getRenderers().clear();
        mapView.addRenderer(qrRenderer);
        mapView.setLocked(true);

        ItemStack itemStack = new ItemStack(Material.FILLED_MAP);
        MapMeta mapMeta = (MapMeta) itemStack.getItemMeta();
        if (mapMeta != null) {
            mapMeta.setMapView(mapView);

            PersistentDataContainer persistentDataContainer = mapMeta.getPersistentDataContainer();
            persistentDataContainer.set(namespacedKey, PersistentDataType.STRING, player.getUniqueId().toString());

            itemStack.setItemMeta(mapMeta);
        }

        return itemStack;
    }

    public void createCodes(int length, int amount) {
        backupCodes.clear();

        String[] codes = CodeGenerator.generateBackupCodes(length, amount);
        for(String code : codes) {
            backupCodes.put(code, false);
        }
    }

    public String getBackupCodes() {
        StringBuilder stringBuilder = new StringBuilder();
        List<String> codes = new ArrayList<>(backupCodes.keySet());
        stringBuilder.append(Chat.parameterize(Locale.BACKUP_CODES_HEADER)).append("\n");
        for (int i = 0; i < codes.size(); i++) {
            String code = codes.get(i);

            String chatFormat = Chat.parameterize(Locale.BACKUP_CODES_FORMAT, String.valueOf(i + 1), code);
            if (backupCodes.get(code)) {
                chatFormat = Chat.parameterize(Locale.BACKUP_CODES_FORMAT, String.valueOf(i + 1), ChatColor.STRIKETHROUGH + code);
            }
            stringBuilder.append(chatFormat).append("\n");
        }
        return stringBuilder.toString();
    }

    public boolean isBackupCode(String code) {
        return backupCodes.containsKey(code);
    }

    public boolean updateCode(String code, boolean used) {
        backupCodes.put(code, used);
        return true;
    }

    public String getSecret() {
        return secret;
    }
}
