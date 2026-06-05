package com.pastdeathghost.client.event;

import com.pastdeathghost.ghost.GhostData;
import com.pastdeathghost.ghost.GhostManager;
import net.minecraft.client.network.ClientPlayerEntity;

import java.util.UUID;

public class DeathDetector {

    public static void onPlayerDeath(ClientPlayerEntity player, String deathMsg) {
        net.minecraft.client.world.ClientWorld world = net.minecraft.client.MinecraftClient.getInstance().world;
        if (player == null || world == null) {
            return;
        }

        double x = player.getX();
        double y = player.getY();
        double z = player.getZ();
        float yaw = player.getYaw();
        float pitch = player.getPitch();
        String dimension = world.getRegistryKey().getValue().toString();

        String formattedMsg = deathMsg;
        String playerName = player.getGameProfile().name();
        if (playerName != null && !playerName.isEmpty()) {
            formattedMsg = deathMsg.replaceAll("(?i)" + java.util.regex.Pattern.quote(playerName), "you");
        }

        GhostData ghost = new GhostData(
                UUID.randomUUID(),
                x,
                y,
                z,
                yaw,
                pitch,
                dimension,
                player.getGameProfile(),
                formattedMsg
        );

        GhostManager.getInstance().addGhost(ghost);
    }
}
