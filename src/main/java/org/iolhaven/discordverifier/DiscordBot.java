package org.iolhaven.discordverifier;

import com.novamaday.d4j.gradle.simplebot.GlobalCommandRegistrar;
import discord4j.core.DiscordClient;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import reactor.core.publisher.Mono;

import java.util.List;

public class DiscordBot {
    private GatewayDiscordClient discordClient;
    private final Usernames users;

    public DiscordBot(Usernames users, Logger logger) {
        this.users = users;

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
            logger.error("Error initializing Discord application commands: {}", e.toString());
        }

        discordClient.on(ChatInputInteractionEvent.class, event -> {
            if (event.getCommandName().equals("verify")) {
                return handleVerification(event);
            }
            return Mono.empty();
        }).subscribe();
    }

    private Mono<Void> handleVerification(ChatInputInteractionEvent event) {
        return event.reply("pong");
    }
}
