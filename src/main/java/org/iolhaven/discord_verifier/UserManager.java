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

// The Fabric mod component of the mod.
public final class UserManager {
    // Singleton
    private static UserManager INSTANCE;
    // JSON (de)serializer
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    // Handle to the list of stored users
    private static final File USER_RECORD = new File(FabricLoader.getInstance().getConfigDir().toFile(), "discord_verifier/users.json");

    private UserManager() {
        // Create a new blank user record if it does not exist already.
        if(!USER_RECORD.exists()) {
            try {
                if (USER_RECORD.getParentFile() != null) {
                    USER_RECORD.getParentFile().mkdirs();
                }
                USER_RECORD.createNewFile();
                try (Writer writer = new FileWriter(USER_RECORD)) {
                    GSON.toJson(new ArrayList<Pair<Snowflake, String>>(), writer);
                }

                DiscordVerifier.LOGGER.debug("Wrote a new blank user record.");
            } catch (Exception e) {
                DiscordVerifier.LOGGER.error("An error occurred when trying to create a configuration file: {}", e.getMessage());
            }
        }
    }

    // Lazily get singleton instance
    public static UserManager getInstance() {
        if(INSTANCE == null) {
            INSTANCE = new UserManager();
        }
        return INSTANCE;
    }

    // Verifies a username as belonging to a Discord account.
    // If the Discord account already had a Minecraft username associated with it, the function will return the name of the previous Minecraft account, which gets overwritten.
    // Otherwise, it returns nothing.
    public Optional<String> writeUsername(Snowflake discord, String minecraft) {
        List<Pair<Snowflake, String>> users;
        Optional<String> out = Optional.empty();
        try (Reader reader = new FileReader(USER_RECORD)) {
            // Load the user record
            users = GSON.fromJson(reader, new TypeToken<>() {});
            out = eraseExistingEntry(users, discord);
            // Add the new user record
            users.add(new Pair<>(discord, minecraft));
        } catch (Exception e) {
            DiscordVerifier.LOGGER.error("Error loading users: {}", e.getMessage());
            return out;
        }

        // Close the read stream and then open a write stream to save the updated configuration.
        try (Writer writer = new FileWriter(USER_RECORD)) {
            GSON.toJson(users, writer);
        }
        catch (Exception e) {
            DiscordVerifier.LOGGER.error("Error writing new user to a file: {}", e.getMessage());
        }

        DiscordVerifier.LOGGER.trace("Stored Discord ID/Minecraft username pair {}/{}.{}", discord, minecraft, out.map(s -> " Overwrote username " + s).orElse(""));

        return out;
    }

    // Removes and returns any existing entries associated with the given Discord ID
    private static Optional<String> eraseExistingEntry(List<Pair<Snowflake, String>> users, Snowflake discord) {
        // There won't be multiple entries matching the condition in the list, so the join() functionality will never get used.
        String out = String.join(", ", users.stream().filter(pair -> pair.getLeft().equals(discord)).map(Pair::getRight).toList());
        users.removeIf(pair -> pair.getLeft().equals(discord));
        // If there was a username in the output string, return it. Otherwise, convert an empty string to an empty Optional.
        return out.isEmpty() ? Optional.empty() : Optional.of(out);
    }

    // Checks to see whether a Minecraft profile has been verified through Discord, and whether it is thus allowed to join.
    public boolean userIsVerified(GameProfile minecraftUser) {
        List<Pair<Snowflake, String>> users;
        try (Reader reader = new FileReader(USER_RECORD)) {
            users = GSON.fromJson(reader, new TypeToken<>() {});
            // If there's any match in the record of verified users, they're verified!
            return users.stream().anyMatch(pair -> matchMinecraftUsername(minecraftUser, pair.getRight()));
        } catch (Exception e) {
            DiscordVerifier.LOGGER.error("Error checking user verification: {}", e.getMessage());
            return false;
        }
    }

    // Checks a Minecraft username to see if it matches a verified username.
    // The Minecraft username will match if it's the same, or starts with the Geyser prefix.
    private boolean matchMinecraftUsername(GameProfile profile, String username) {
        return profile.getName().matches("(%s)?%s".formatted(ModConfig.getInstance().getGeyserUserPrefix(), username));
    }
}
