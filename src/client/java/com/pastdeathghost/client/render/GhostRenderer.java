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

        for (GhostData ghost : GhostManager.getInstance().getGhosts()) {
            if (!ghost.getDimension().equals(currentDim)) {
                if (spawnedEntities.containsKey(ghost.getId())) {
                    GhostPlayerEntity entity = spawnedEntities.remove(ghost.getId());
                    if (entity != null) {
                        entity.discard();
                    }
                }
                continue;
            }

            if (!spawnedEntities.containsKey(ghost.getId())) {
                com.mojang.authlib.GameProfile originalProfile = ghost.getProfile();
                java.util.UUID newUuid = java.util.UUID.randomUUID();
                com.google.common.collect.Multimap<String, com.mojang.authlib.properties.Property> backingMap = com.google.common.collect.LinkedHashMultimap.create(originalProfile.properties());
                com.mojang.authlib.properties.PropertyMap newProperties = new com.mojang.authlib.properties.PropertyMap(backingMap);
                com.mojang.authlib.GameProfile newProfile = new com.mojang.authlib.GameProfile(newUuid, originalProfile.name(), newProperties);

                GhostPlayerEntity entity = new GhostPlayerEntity(world, newProfile);
                entity.setPos(ghost.getX(), ghost.getY(), ghost.getZ());
                entity.setYaw(ghost.getYaw());
                entity.setPitch(ghost.getPitch());
                entity.setHeadYaw(ghost.getYaw());
                entity.setBodyYaw(ghost.getYaw());
                entity.lastYaw = ghost.getYaw();
                entity.lastPitch = ghost.getPitch();
                entity.lastHeadYaw = ghost.getYaw();
                entity.lastBodyYaw = ghost.getYaw();
                
                entity.setCustomName(Text.literal(ghost.getDeathMessage()));
                entity.setCustomNameVisible(true);

                entity.noClip = true;

                world.addEntity(entity);
                spawnedEntities.put(ghost.getId(), entity);
            } else {
                GhostPlayerEntity entity = spawnedEntities.get(ghost.getId());
                if (entity != null) {
                    if (entity.isRemoved() || world.getEntityById(entity.getId()) == null) {
                        spawnedEntities.remove(ghost.getId());
                    } else {
                        entity.setPos(ghost.getX(), ghost.getY(), ghost.getZ());
                        entity.setYaw(ghost.getYaw());
                        entity.setPitch(ghost.getPitch());
                        entity.setHeadYaw(ghost.getYaw());
                        entity.setBodyYaw(ghost.getYaw());
                        entity.setVelocity(0, 0, 0);
                        entity.noClip = true;
                        entity.setNoGravity(true);
                    }
                }
            }
        }

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
