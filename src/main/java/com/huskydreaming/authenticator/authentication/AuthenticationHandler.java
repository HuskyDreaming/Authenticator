package com.huskydreaming.authenticator.authentication;

import com.google.gson.reflect.TypeToken;
import com.huskydreaming.authenticator.code.CodeGenerator;
import com.huskydreaming.authenticator.code.CodeVerifier;
import com.huskydreaming.authenticator.utilities.Json;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Type;
import java.time.Instant;
import java.util.Collections;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class AuthenticationHandler {

    private final Plugin plugin;
    private final NamespacedKey namespacedKey;

    private Map<UUID, Authentication> authentications = new ConcurrentHashMap<>();

    public AuthenticationHandler(Plugin plugin) {
        this.plugin = plugin;
        this.namespacedKey = new NamespacedKey(plugin, "Authentication");
    }

    public void deserialize() {
        Type type = new TypeToken<Map<UUID, Authentication>>() {}.getType();
        Map<UUID, Authentication> authentications = Json.read(plugin, "authentications", type);

        this.authentications = authentications != null ? authentications : new ConcurrentHashMap<>();
    }

    public void serialize() {
        Json.write(plugin, "authentications", authentications);
    }

    public void verify(Player player, Authentication authentication) {
        authentications.put(player.getUniqueId(), authentication);
    }

    public boolean isVerified(Authentication authentication, String code) {
        CodeGenerator codeGenerator = new CodeGenerator();
        CodeVerifier verifier = new CodeVerifier(codeGenerator, Instant.now().getEpochSecond());

        return verifier.isValid(authentication.getSecret(), code);
    }

    public boolean isVerified(OfflinePlayer offlinePlayer) {
        return authentications.containsKey(offlinePlayer.getUniqueId());
    }


    public void remove(OfflinePlayer offlinePlayer) {
        authentications.remove(offlinePlayer.getUniqueId());
    }

    public boolean isItem(ItemStack itemStack) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta == null) return false;

        PersistentDataContainer persistentDataContainer = itemMeta.getPersistentDataContainer();
        return persistentDataContainer.has(namespacedKey, PersistentDataType.STRING);
    }

    public void cleanup( Player player ) {
        for(int i = 0; i < player.getInventory().getSize(); i++) {
            ItemStack itemStack = player.getInventory().getItem(i);
            if(itemStack != null) {
                if(isItem(itemStack)) {
                    player.getInventory().setItem(i, new ItemStack(Material.AIR));
                }
            }
        }
    }

    public Map<UUID, Authentication> getAuthentications() {
        return Collections.unmodifiableMap(authentications);
    }

    public NamespacedKey getNamespacedKey() {
        return namespacedKey;
    }
}
