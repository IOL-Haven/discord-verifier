package org.iolhaven.discordverifier;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import net.minecraft.server.Whitelist;
import net.minecraft.util.Pair;
import org.slf4j.Logger;

import java.io.*;
import java.util.List;

public class Usernames {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private final File CONFIG_FILE;
    private final Logger LOGGER;

    public Usernames(File config_file, Logger logger) {
        CONFIG_FILE = config_file;
        LOGGER = logger;

        try {
            CONFIG_FILE.createNewFile();
        }
        catch (Exception e) {
            LOGGER.error("An error occurred when trying to create a configuration file: {}", config_file);
        }
    }

    private void writeUsername(String discord, String minecraft) {
        List<Pair<String, String>> users;
        try (Reader reader = new FileReader(CONFIG_FILE)) {
            users = GSON.fromJson(reader, new TypeToken<List<Pair<String, String>>>() {});
            String previous_name = eraseExistingEntry(users, discord);
            users.add(new Pair<>(discord, minecraft));
        } catch (Exception e) {
            LOGGER.error("Error loading users: {}", e.getMessage());
            return;
        }

        try (Writer writer = new FileWriter(CONFIG_FILE)) {
            GSON.toJson(users, writer);
        }
        catch (Exception e) {
            LOGGER.error("Error loading users: {}", e.getMessage());
        }
    }

    private String eraseExistingEntry(List<Pair<String, String>> users, String discord) {
        String out = String.join(", ", users.stream().filter(pair -> pair.getLeft().equals(discord)).map(Pair::getRight).toList());
        users.removeIf(pair -> pair.getLeft().equals(discord));
        return out;
    }
}
