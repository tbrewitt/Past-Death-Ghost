package com.pastdeathghost.client;

import com.pastdeathghost.client.command.GhostCommand;
import com.pastdeathghost.client.render.GhostRenderer;
import net.fabricmc.api.ClientModInitializer;

/**
 * Main client-side entrypoint for the Past Death Ghost mod.
 * Initializes event listeners, commands, and rendering systems.
 */
public class PastDeathGhostClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        GhostRenderer.register();
        GhostCommand.register();
    }
}
