package org.iolhaven.discord_verifier;

import com.novamaday.d4j.gradle.simplebot.GlobalCommandRegistrar;
import discord4j.common.util.Snowflake;
import discord4j.core.DiscordClient;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.object.command.ApplicationCommandInteractionOption;
import discord4j.core.object.command.ApplicationCommandInteractionOptionValue;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Optional;

public class DiscordBot {
    private GatewayDiscordClient discordClient;

    public DiscordBot() {
        DiscordClient client = DiscordClient.create("TOKEN");
        Mono<Void> login = client.withGateway(gateway ->
        {
            discordClient = gateway;
            return Mono.empty();
        });
        login.block();

        try {
            new GlobalCommandRegistrar(discordClient.getRestClient()).registerCommands(List.of("verify.json"));
        } catch (Exception e) {
            DiscordVerifier.LOGGER.error("Error initializing Discord application commands: {}", e.getMessage());
        }

        discordClient.on(ChatInputInteractionEvent.class, event -> {
            if (event.getCommandName().equals("verify")) {
                return handleVerification(event);
            }
            return Mono.empty();
        }).subscribe();
    }

    private Mono<Void> handleVerification(ChatInputInteractionEvent event) {
        StringBuilder reply = new StringBuilder();
        Snowflake discord = event.getInteraction().getUser().getId();
        String discord_name = event.getInteraction().getUser().getUsername();
        String username = event.getOption("username")
                .flatMap(ApplicationCommandInteractionOption::getValue)
                .map(ApplicationCommandInteractionOptionValue::asString)
                .orElse("");

        Optional<String> userManagerResponse = UserManager.getInstance().writeUsername(discord, username);
        userManagerResponse.ifPresent(s -> reply.append("You were already verified under the Minecraft username %s. It will be erased and replaced with the username %s. No player data will be lost.\n".formatted(s, username)));

        reply.append("Successfully verified the Minecraft account %s as belonging to Discord user %s!\n\n*You may now log onto the server.*".formatted(username, discord_name));

        return event.reply(reply.toString());
    }
}
