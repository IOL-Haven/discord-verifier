package org.iolhaven.discordverifier;

import net.fabricmc.loader.api.FabricLoader;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.slf4j.Logger;

import java.io.*;

public class ModConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final File CONFIG_FILE = new File(FabricLoader.getInstance().getConfigDir().toFile(), "discordverifier/config.json");

    private final String geyserUserPrefix = ".";

    public static ModConfig loadConfig(Logger logger) {
        ModConfig config;
        if (CONFIG_FILE.exists()) {
            try (Reader reader = new FileReader(CONFIG_FILE)) {
                config = GSON.fromJson(reader, ModConfig.class);
            } catch (Exception e) {
                logger.error("Error loading config: {}", e.getMessage());
                config = new ModConfig();
            }
        } else {
            config = new ModConfig();
        }
        logger.info("Loaded mod config.");
        return config;
    }

    public String getGeyserUserPrefix() {
        return geyserUserPrefix;
    }
}