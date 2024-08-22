package org.iolhaven.discord_verifier;

import com.novamaday.d4j.gradle.simplebot.GlobalCommandRegistrar;
import discord4j.common.util.Snowflake;
import discord4j.core.DiscordClient;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.object.command.ApplicationCommandInteractionOption;
import discord4j.core.object.command.ApplicationCommandInteractionOptionValue;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Optional;

// The Discord bot component of the mod.
// Runs asynchronously in the background and communicates with UserManager to pass information from Discord to Minecraft.
class DiscordBot {
    // Initialize and launch the bot
    public DiscordBot() {
        try {
            // Create a client and register it with the token from the config.
            DiscordClient client = DiscordClient.create(ModConfig.getInstance().getDiscordToken());
            // Create an active connection with Discord
            client.withGateway(gateway ->
            {
                // Register command
                try {
                    new GlobalCommandRegistrar(gateway.getRestClient()).registerCommands(List.of("verify.json"));
                } catch (Exception e) {
                    DiscordVerifier.LOGGER.error("Error initializing Discord application commands: {}", e.getMessage());
                }

                // Indefinitely handle the /verify command
                return gateway.on(ChatInputInteractionEvent.class, event -> {
                    if (event.getCommandName().equals("verify")) {
                        return handleVerification(event);
                    }
                    return Mono.empty();
                });
            }).subscribe(); // Run in the background

            DiscordVerifier.LOGGER.debug("Discord bot initialized!");
        }
        catch (Exception e) {
            DiscordVerifier.LOGGER.error("Invalid Discord app token!");
        }
    }

    // Respond to the /verify command
    private Mono<Void> handleVerification(ChatInputInteractionEvent event) {
        StringBuilder reply = new StringBuilder(); // What will be sent to the user

        // Get the Discord account ID of the interacting user, and the Minecraft username they want to register
        Snowflake discord_id = event.getInteraction().getUser().getId();
        String minecraft_username = event.getOption("username")
                .flatMap(ApplicationCommandInteractionOption::getValue)
                .map(ApplicationCommandInteractionOptionValue::asString)
                .orElse("");

        // Store the new discord id + username pair.
        Optional<String> userManagerResponse = UserManager.getInstance().writeUsername(discord_id, minecraft_username);
        // Add a success message to the reply
        reply.append("Successfully verified the Minecraft account %s as belonging to Discord user %s!\n\n**You may now log onto the server.**".formatted(minecraft_username, event.getInteraction().getUser().getUsername()));
        // If there was already an account registered for this user, there will be a message that it was unregistered to make room for the new one.
        // That message will get added to the beginning of the reply.
        userManagerResponse.ifPresent(s -> reply.append("\n\n*You were already verified under the Minecraft username %s. That **verification data** will be overwritten by the Minecraft username %s. **No player data will be lost.***".formatted(s, minecraft_username)));

        DiscordVerifier.LOGGER.trace("Handled verification of user {}", event.getInteraction().getUser().getUsername());
        // Send the reply back to the user who sent the command
        return event.reply(reply.toString());
    }
}
