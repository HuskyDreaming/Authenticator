package com.huskydreaming.authenticator.utilities;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;
import org.bukkit.plugin.Plugin;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Json {

    private static final Gson GSON = new GsonBuilder()
            .setPrettyPrinting()
            .create();

    public static void write(Plugin plugin, String fileName, Object object) {
        Path path = check(plugin, fileName);

        try (BufferedWriter bufferedWriter = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
            GSON.toJson(object, bufferedWriter);
            plugin.getLogger().info("Serialized " + path.getFileName() + " successfully.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static <T> T read(Plugin plugin, String fileName, Type type) {
        Path path = check(plugin, fileName);
        try {
            BufferedReader bufferedReader = Files.newBufferedReader(path, StandardCharsets.UTF_8);
            JsonReader jsonReader = new JsonReader(bufferedReader);
            T t = GSON.fromJson(jsonReader, type);
            plugin.getLogger().info("Deserialized " + path.getFileName() + " successfully.");
            return t;
        } catch (IOException e) {
            return null;
        }
    }

    private static Path check(Plugin plugin, String fileName) {
        Path path = Paths.get(plugin.getDataFolder() + "/" + fileName + ".json");
        try {
            if(!Files.exists(plugin.getDataFolder().toPath())) {
                Files.createDirectories(plugin.getDataFolder().toPath());
                plugin.getLogger().info("Creating new directory: " + plugin.getDataFolder());
            }

            if (!Files.exists(path)) {
                Files.createFile(path);
                plugin.getLogger().info("Creating new file: " + path.getFileName());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return path;
    }
}
