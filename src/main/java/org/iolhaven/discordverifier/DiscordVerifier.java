package org.iolhaven.discordverifier;

import net.fabricmc.api.ModInitializer;

import net.fabricmc.loader.api.FabricLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

public class DiscordVerifier implements ModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger("discord-verifier");
	private static final File CONFIG_FILE = new File(FabricLoader.getInstance().getConfigDir().toFile(), "discordverifier/users.json");

	@Override
	public void onInitialize() {
		LOGGER.debug("Starting initialization of DiscordVerifier");

		Usernames users = new Usernames(CONFIG_FILE, LOGGER);
		DiscordBot bot = new DiscordBot(users, LOGGER);
	}
}