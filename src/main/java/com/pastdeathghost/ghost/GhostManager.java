package com.pastdeathghost.ghost;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.fabricmc.loader.api.FabricLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Manages loading, saving, and enforcing bounds on the active list of ghosts.
 */
public class GhostManager {
    private static final Logger LOGGER = LoggerFactory.getLogger("PastDeathGhost");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path CONFIG_PATH = FabricLoader.getInstance().getConfigDir().resolve("past_death_ghost.json");

    private final List<GhostData> ghosts = new ArrayList<>();
    private int maxGhosts = 10; // Default limit

    private static final GhostManager INSTANCE = new GhostManager();

    private GhostManager() {
        load();
    }

    public static GhostManager getInstance() {
        return INSTANCE;
    }

    public synchronized List<GhostData> getGhosts() {
        return new ArrayList<>(ghosts);
    }

    public synchronized int getMaxGhosts() {
        return maxGhosts;
    }

    public synchronized void setMaxGhosts(int max) {
        this.maxGhosts = max;
        enforceLimit();
        save();
    }

    public synchronized void addGhost(GhostData ghost) {
        ghosts.add(ghost);
        enforceLimit();
        save();
    }

    public synchronized void clearGhosts() {
        ghosts.clear();
        save();
    }

    private void enforceLimit() {
        while (ghosts.size() > maxGhosts && !ghosts.isEmpty()) {
            ghosts.remove(0); // Remove oldest
        }
    }

    /**
     * Loads the config and ghost list from disk.
     */
    public synchronized void load() {
        File file = CONFIG_PATH.toFile();
        if (!file.exists()) {
            save(); // Create default config file
            return;
        }

        try (FileReader reader = new FileReader(file)) {
            JsonObject root = JsonParser.parseReader(reader).getAsJsonObject();
            if (root.has("maxGhosts")) {
                this.maxGhosts = root.get("maxGhosts").getAsInt();
            }
            this.ghosts.clear();
            if (root.has("ghosts")) {
                JsonArray arr = root.getAsJsonArray("ghosts");
                for (JsonElement el : arr) {
                    try {
                        this.ghosts.add(GhostData.fromJson(el.getAsJsonObject()));
                    } catch (Exception e) {
                        LOGGER.error("Failed to parse individual ghost entry", e);
                    }
                }
            }
            enforceLimit();
        } catch (Exception e) {
            LOGGER.error("Failed to load ghost config file", e);
        }
    }

    /**
     * Saves the current config and ghost list to disk.
     */
    public synchronized void save() {
        JsonObject root = new JsonObject();
        root.addProperty("maxGhosts", maxGhosts);

        JsonArray arr = new JsonArray();
        for (GhostData ghost : ghosts) {
            arr.add(ghost.toJson());
        }
        root.add("ghosts", arr);

        File file = CONFIG_PATH.toFile();
        File parent = file.getParentFile();
        if (parent != null && !parent.exists()) {
            parent.mkdirs();
        }

        try (FileWriter writer = new FileWriter(file)) {
            GSON.toJson(root, writer);
        } catch (Exception e) {
            LOGGER.error("Failed to save ghost config file", e);
        }
    }
}
