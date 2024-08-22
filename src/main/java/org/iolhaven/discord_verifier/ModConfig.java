package org.iolhaven.discord_verifier;

import net.fabricmc.loader.api.FabricLoader;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.*;

// The ModConfig
class ModConfig {
    // Singleton
    private static ModConfig INSTANCE;
    // JSON (de)serializer
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    // Config file handle
    private static final File CONFIG_FILE = new File(FabricLoader.getInstance().getConfigDir().toFile(), "discord_verifier/config.json");

    // The prefix that is set in Geyser/Floodgate's config that gets appended to Bedrock usernames.
    private String geyserUserPrefix = "";
    // The Discord bot token needed to connect it to Discord.
    private String discordToken = "";

    // Lazily get singleton instance
    public static ModConfig getInstance() {
        if(INSTANCE == null) {
            INSTANCE = loadConfig();
        }
        return INSTANCE;
    }

    // Reads the ModConfig from the disk configuration file.
    // Does not support hot reloading.
    private static ModConfig loadConfig() {
        ModConfig config;
        if (CONFIG_FILE.exists()) {
            try (Reader reader = new FileReader(CONFIG_FILE)) {
                config = GSON.fromJson(reader, ModConfig.class);
            } catch (Exception e) {
                DiscordVerifier.LOGGER.error("Error loading config: {}", e.getMessage());
                config = new ModConfig();
            }
        } else { // Write a blank config file to disk for the server admin to populate.
            config = new ModConfig();
            try (Writer writer = new FileWriter(CONFIG_FILE)) {
                if(CONFIG_FILE.getParentFile() != null) {
                    CONFIG_FILE.getParentFile().mkdirs();
                }
                CONFIG_FILE.createNewFile();
                GSON.toJson(config, writer);

                DiscordVerifier.LOGGER.debug("Wrote new blank mod config.");
            }
            catch (Exception e) {
                DiscordVerifier.LOGGER.error("Error writing blank config: {}", e.getMessage());
            }
        }
        DiscordVerifier.LOGGER.debug("Loaded mod config.");
        return config;
    }

    public String getGeyserUserPrefix() {
        return geyserUserPrefix;
    }
    public String getDiscordToken() { return discordToken; }
}