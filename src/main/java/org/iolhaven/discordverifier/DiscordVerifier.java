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

		DiscordBot bot = new DiscordBot();

		LOGGER.debug("DiscordVerifier finished initializing.");
	}
}