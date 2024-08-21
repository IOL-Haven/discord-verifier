package org.iolhaven.discordverifier;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.loader.api.FabricLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileReader;
import java.io.Reader;

public class DiscordVerifier implements ModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger("discord-verifier");
	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
	private static final File CONFIG_FILE = new File(FabricLoader.getInstance().getConfigDir().toFile(), "discordverifier/users.json");

	@Override
	public void onInitialize() {
		LOGGER.debug("Starting initialization of DiscordVerifier");

		Usernames users;
		if (CONFIG_FILE.exists()) {
			try (Reader reader = new FileReader(CONFIG_FILE)) {
				users = GSON.fromJson(reader, Usernames.class);
			} catch (Exception e) {
				System.err.println("Error loading users: " + e.getMessage());
				users = new Usernames();
			}
		} else {
			users = new Usernames();
		}

		DiscordBot bot = new DiscordBot(users, LOGGER);
	}
}