package com.pastdeathghost.client.render;

import com.pastdeathghost.client.entity.GhostPlayerEntity;
import com.pastdeathghost.ghost.GhostData;
import com.pastdeathghost.ghost.GhostManager;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.text.Text;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class GhostRenderer {
    private static final Map<UUID, GhostPlayerEntity> spawnedEntities = new HashMap<>();
    private static ClientWorld cachedWorld = null;

    public static void register() {
        ClientTickEvents.END_CLIENT_TICK.register(GhostRenderer::onClientTick);
    }

    private static void onClientTick(MinecraftClient client) {
        ClientWorld world = client.world;
        if (world == null) {
            spawnedEntities.clear();
            cachedWorld = null;
            return;
        }

        if (cachedWorld != world) {
            spawnedEntities.clear();
            cachedWorld = world;
        }

        String currentDim = world.getRegistryKey().getValue().toString();

        // 1. Spawn ghosts that are not yet spawned
        for (GhostData ghost : GhostManager.getInstance().getGhosts()) {
            if (!ghost.getDimension().equals(currentDim)) {
                // If it is spawned but in the wrong dimension, discard it
                if (spawnedEntities.containsKey(ghost.getId())) {
                    GhostPlayerEntity entity = spawnedEntities.remove(ghost.getId());
                    if (entity != null) {
                        entity.discard();
                    }
                }
                continue;
            }

            if (!spawnedEntities.containsKey(ghost.getId())) {
                GhostPlayerEntity entity = new GhostPlayerEntity(world, ghost.getProfile());
                entity.setPos(ghost.getX(), ghost.getY(), ghost.getZ());
                entity.setYaw(ghost.getYaw());
                entity.setPitch(ghost.getPitch());
                entity.setHeadYaw(ghost.getYaw());
                entity.setBodyYaw(ghost.getYaw());
                entity.lastYaw = ghost.getYaw();
                entity.lastPitch = ghost.getPitch();
                entity.lastHeadYaw = ghost.getYaw();
                entity.lastBodyYaw = ghost.getYaw();
                
                // Show death message instead of name
                entity.setCustomName(Text.literal(ghost.getDeathMessage()));
                entity.setCustomNameVisible(true);
                
                // Disable collision/physics
                entity.noClip = true;
                
                // Add to world client-side
                world.addEntity(entity);
                spawnedEntities.put(ghost.getId(), entity);
            } else {
                // Keep it in place and check if it's still in the world
                GhostPlayerEntity entity = spawnedEntities.get(ghost.getId());
                if (entity != null && (entity.isRemoved() || world.getEntityById(entity.getId()) == null)) {
                    // Evict from cache so a new entity gets spawned next tick
                    spawnedEntities.remove(ghost.getId());
                }
            }
        }

        // 2. Remove ghosts that no longer exist in GhostManager
        spawnedEntities.entrySet().removeIf(entry -> {
            UUID id = entry.getKey();
            GhostPlayerEntity entity = entry.getValue();
            boolean stillExists = false;
            for (GhostData ghost : GhostManager.getInstance().getGhosts()) {
                if (ghost.getId().equals(id) && ghost.getDimension().equals(currentDim)) {
                    stillExists = true;
                    break;
                }
            }
            if (!stillExists) {
                if (entity != null) {
                    entity.discard();
                }
                return true;
            }
            return false;
        });
    }
}
