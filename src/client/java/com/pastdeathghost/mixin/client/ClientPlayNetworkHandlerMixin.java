package com.pastdeathghost.mixin.client;

import com.pastdeathghost.client.event.DeathDetector;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.s2c.play.DeathMessageS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayNetworkHandler.class)
public class ClientPlayNetworkHandlerMixin {

    @Inject(method = "onDeathMessage", at = @At("TAIL"))
    private void past_death_ghost$onDeathMessage(DeathMessageS2CPacket packet, CallbackInfo ci) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player != null) {
            System.out.println("[PastDeathGhost] Received death packet. Target entity ID: " + packet.playerId() + ", local player entity ID: " + client.player.getId());
            if (packet.playerId() == client.player.getId()) {
                String deathMsg = packet.message().getString();
                System.out.println("[PastDeathGhost] Processing local player death. Message: \"" + deathMsg + "\"");
                DeathDetector.onPlayerDeath(client.player, deathMsg);
            }
        }
    }
}
