package org.iolhaven.discordverifier;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.mojang.authlib.GameProfile;
import discord4j.common.util.Snowflake;
import net.minecraft.util.Pair;
import org.slf4j.Logger;

import java.io.*;
import java.util.List;
import java.util.Optional;

public class UserManager {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private final File USER_RECORD;
    private final ModConfig CONFIG;
    private final Logger LOGGER;

    public UserManager(File user_record, ModConfig config, Logger logger) {
        USER_RECORD = user_record;
        CONFIG = config;
        LOGGER = logger;

        try {
            USER_RECORD.createNewFile();
        }
        catch (Exception e) {
            LOGGER.error("An error occurred when trying to create a configuration file: {}", user_record);
        }
    }

    public Optional<String> writeUsername(Snowflake discord, String minecraft) {
        List<Pair<Snowflake, String>> users;
        Optional<String> out = Optional.empty();
        try (Reader reader = new FileReader(USER_RECORD)) {
            users = GSON.fromJson(reader, new TypeToken<>() {});
            out = Optional.of(eraseExistingEntry(users, discord));
            users.add(new Pair<>(discord, minecraft));
        } catch (Exception e) {
            LOGGER.error("Error loading users: {}", e.getMessage());
            return out;
        }

        try (Writer writer = new FileWriter(USER_RECORD)) {
            GSON.toJson(users, writer);
        }
        catch (Exception e) {
            LOGGER.error("Error loading users: {}", e.getMessage());
        }

        return out;
    }

    private static String eraseExistingEntry(List<Pair<Snowflake, String>> users, Snowflake discord) {
        String out = String.join(", ", users.stream().filter(pair -> pair.getLeft().equals(discord)).map(Pair::getRight).toList());
        users.removeIf(pair -> pair.getLeft().equals(discord));
        return out;
    }

    public boolean userIsVerified(GameProfile minecraftUser) {
        List<Pair<String, String>> users;
        try (Reader reader = new FileReader(USER_RECORD)) {
            users = GSON.fromJson(reader, new TypeToken<>() {});
            return users.stream().anyMatch(pair -> matchMinecraftUsername(minecraftUser, pair.getRight()));
        } catch (Exception e) {
            LOGGER.error("Error loading users: {}", e.getMessage());
            return false;
        }
    }

    private boolean matchMinecraftUsername(GameProfile profile, String username) {
        return profile.getName().matches("(%s)?%s".formatted(CONFIG.getGeyserUserPrefix(), username));
    }
}
