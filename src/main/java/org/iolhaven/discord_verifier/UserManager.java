package org.iolhaven.discord_verifier;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.mojang.authlib.GameProfile;
import discord4j.common.util.Snowflake;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.util.Pair;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserManager {
    private static UserManager INSTANCE;
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final File USER_RECORD = new File(FabricLoader.getInstance().getConfigDir().toFile(), "discord_verifier/users.json");

    private UserManager() {
        if(!USER_RECORD.exists()) {
            try {
                if (USER_RECORD.getParentFile() != null) {
                    USER_RECORD.getParentFile().mkdirs();
                }
                USER_RECORD.createNewFile();
                try (Writer writer = new FileWriter(USER_RECORD)) {
                    GSON.toJson(new ArrayList<Pair<Snowflake, String>>(), writer);
                }
            } catch (Exception e) {
                DiscordVerifier.LOGGER.error("An error occurred when trying to create a configuration file: {}", e.getMessage());
            }
        }
    }

    public static UserManager getInstance() {
        if(INSTANCE == null) {
            INSTANCE = new UserManager();
        }
        return INSTANCE;
    }

    public Optional<String> writeUsername(Snowflake discord, String minecraft) {
        List<Pair<Snowflake, String>> users;
        Optional<String> out = Optional.empty();
        try (Reader reader = new FileReader(USER_RECORD)) {
            users = GSON.fromJson(reader, new TypeToken<>() {});
            out = eraseExistingEntry(users, discord);
            users.add(new Pair<>(discord, minecraft));
        } catch (Exception e) {
            DiscordVerifier.LOGGER.error("Error loading users: {}", e.getMessage());
            return out;
        }

        try (Writer writer = new FileWriter(USER_RECORD)) {
            GSON.toJson(users, writer);
        }
        catch (Exception e) {
            DiscordVerifier.LOGGER.error("Error writing new user to a file: {}", e.getMessage());
        }

        return out;
    }

    private static Optional<String> eraseExistingEntry(List<Pair<Snowflake, String>> users, Snowflake discord) {
        String out = String.join(", ", users.stream().filter(pair -> pair.getLeft().equals(discord)).map(Pair::getRight).toList());
        users.removeIf(pair -> pair.getLeft().equals(discord));
        return out.isEmpty() ? Optional.empty() : Optional.of(out);
    }

    public boolean userIsVerified(GameProfile minecraftUser) {
        List<Pair<Snowflake, String>> users;
        try (Reader reader = new FileReader(USER_RECORD)) {
            users = GSON.fromJson(reader, new TypeToken<>() {});
            return users.stream().anyMatch(pair -> matchMinecraftUsername(minecraftUser, pair.getRight()));
        } catch (Exception e) {
            DiscordVerifier.LOGGER.error("Error checking user verification: {}", e.getMessage());
            return false;
        }
    }

    private boolean matchMinecraftUsername(GameProfile profile, String username) {
        return profile.getName().matches("(%s)?%s".formatted(ModConfig.getInstance().getGeyserUserPrefix(), username));
    }
}
