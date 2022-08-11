package com.huskydreaming.authenticator.utilities;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.*;
import java.nio.charset.StandardCharsets;


public class Yaml {

    private final String name;
    private File file;
    private FileConfiguration configuration;

    public Yaml(String name) {
        this.name = name;
    }

    public void load(Plugin plugin) {
        file = new File(plugin.getDataFolder() + File.separator + getFileName());
        try {
            if (file.getParentFile().mkdirs()) {
                plugin.getLogger().info("Creating new directory: " + plugin.getDataFolder());
            }

            if (file.createNewFile()) {
                plugin.getLogger().info("Creating new file: " + getFileName());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        configuration = YamlConfiguration.loadConfiguration(file);
        plugin.getLogger().info("Deserialized " + getFileName() + " successfully.");
    }

    public void save() {
        if (configuration == null || file == null || !file.exists()) return;
        try {
            configuration.save(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void reload(Plugin plugin) {
        configuration = YamlConfiguration.loadConfiguration(file);
        InputStream inputStream = plugin.getResource(getFileName());

        if (inputStream == null) return;

        Reader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
        YamlConfiguration defaultConfiguration = YamlConfiguration.loadConfiguration(reader);
        configuration.setDefaults(defaultConfiguration);

    }

    public FileConfiguration getConfiguration() {
        return configuration;
    }

    private String getFileName() {
        return name + ".yml";
    }
}

