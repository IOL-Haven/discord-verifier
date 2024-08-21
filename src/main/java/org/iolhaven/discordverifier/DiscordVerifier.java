package org.iolhaven.discordverifier;

import net.fabricmc.api.ModInitializer;

import net.fabricmc.loader.api.FabricLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

public class DiscordVerifier implements ModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger("discord-verifier");

	@Override
	public void onInitialize() {
		LOGGER.debug("Starting initialization of DiscordVerifier");

		ModConfig config = ModConfig.loadConfig(LOGGER);
		UserManager users = new UserManager(new File(FabricLoader.getInstance().getConfigDir().toFile(), "discordverifier/users.json"), config, LOGGER);
		DiscordBot bot = new DiscordBot(users, LOGGER);

		LOGGER.debug("DiscordVerifier finished initializing.");
	}
}