package com.huskydreaming.authenticator.authentication;

import com.huskydreaming.authenticator.qr.QrData;
import com.huskydreaming.authenticator.qr.QrGenerator;
import com.huskydreaming.authenticator.qr.QrRenderer;
import org.apache.commons.codec.binary.Base32;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.MapMeta;
import org.bukkit.map.MapView;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.security.SecureRandom;

public class Authentication {

    private final String secret;
    private String[] backupCodes;

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

    public String getSecret() {
        return secret;
    }

    public String[] getBackupCodes() {
        return backupCodes;
    }

    public void setBackupCodes(String[] backupCodes) {
        this.backupCodes = backupCodes;
    }
}
