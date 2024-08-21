package org.iolhaven.discord_verifier;

import net.fabricmc.api.ModInitializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DiscordVerifier implements ModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger("discord-verifier");

	@Override
	public void onInitialize() {
		LOGGER.debug("Starting initialization of DiscordVerifier");

		DiscordBot bot = new DiscordBot();

		LOGGER.info("DiscordVerifier finished initializing.");
	}
}