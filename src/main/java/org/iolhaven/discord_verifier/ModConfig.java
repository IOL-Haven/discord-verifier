package org.iolhaven.discord_verifier;

import net.fabricmc.loader.api.FabricLoader;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.*;

final class ModConfig {
    private static ModConfig INSTANCE;
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final File CONFIG_FILE = new File(FabricLoader.getInstance().getConfigDir().toFile(), "discord_verifier/config.json");

    private final String geyserUserPrefix = ".";
    private final String discordToken = "";

    public static ModConfig getInstance() {
        if(INSTANCE == null) {
            INSTANCE = loadConfig();
        }
        return INSTANCE;
    }

    private static ModConfig loadConfig() {
        ModConfig config;
        if (CONFIG_FILE.exists()) {
            try (Reader reader = new FileReader(CONFIG_FILE)) {
                config = GSON.fromJson(reader, ModConfig.class);
            } catch (Exception e) {
                DiscordVerifier.LOGGER.error("Error loading config: {}", e.getMessage());
                config = new ModConfig();
            }
        } else {
            config = new ModConfig();
            try (Writer writer = new FileWriter(CONFIG_FILE)) {
                GSON.toJson(config, writer);
            }
            catch (Exception e) {
                DiscordVerifier.LOGGER.error("Error loading config: {}", e.getMessage());
            }
        }
        DiscordVerifier.LOGGER.info("Loaded mod config.");
        return config;
    }

    public String getGeyserUserPrefix() {
        return geyserUserPrefix;
    }
    public String getDiscordToken() { return discordToken; }
}