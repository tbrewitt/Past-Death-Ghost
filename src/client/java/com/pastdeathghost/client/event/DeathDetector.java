package com.pastdeathghost.client.event;

import com.pastdeathghost.ghost.GhostData;
import com.pastdeathghost.ghost.GhostManager;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;

import java.util.UUID;

/**
 * Detects client player death transitions and registers new death ghosts.
 */
public class DeathDetector {
    private static boolean wasDead = false;

    public static void register() {
        ClientTickEvents.END_CLIENT_TICK.register(DeathDetector::onClientTick);
    }

    private static void onClientTick(MinecraftClient client) {
        ClientPlayerEntity player = client.player;
        if (player == null) {
            wasDead = false;
            return;
        }

        // Detect if player is dead (either health is zero, or marked as dead)
        boolean isCurrentlyDead = player.isDead() || player.getHealth() <= 0.0f;

        if (isCurrentlyDead && !wasDead) {
            // Retrieve death context
            double x = player.getX();
            double y = player.getY();
            double z = player.getZ();
            float yaw = player.getYaw();
            float pitch = player.getPitch();
            String dimension = client.world.getRegistryKey().getValue().toString();

            String deathMsg = player.getDamageTracker().getDeathMessage().getString();

            // Create and record ghost data
            GhostData ghost = new GhostData(
                    UUID.randomUUID(),
                    x,
                    y,
                    z,
                    yaw,
                    pitch,
                    dimension,
                    player.getGameProfile(),
                    deathMsg
            );

            GhostManager.getInstance().addGhost(ghost);
        }

        wasDead = isCurrentlyDead;
    }
}
