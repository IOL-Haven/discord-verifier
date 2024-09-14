package org.iolhaven.discord_verifier;

import net.fabricmc.api.ModInitializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// The DiscordVerifier mod entrypoint.
// It launches the Discord bot component of the mod, which then updates the Minecraft server when a verification command is sent.
// The player authentication checker has been given a mixin so that it checks whether the player is Discord verified.
public class DiscordVerifier implements ModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger("discord-verifier");

	@Override
	public void onInitialize() {
		LOGGER.debug("Starting initialization of DiscordVerifier");

		DiscordBot bot = new DiscordBot();

		LOGGER.info("DiscordVerifier initialization complete.");
	}
}