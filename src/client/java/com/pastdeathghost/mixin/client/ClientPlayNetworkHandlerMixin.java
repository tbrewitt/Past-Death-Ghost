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
            if (packet.playerId() == client.player.getId()) {
                String deathMsg = packet.message().getString();
                DeathDetector.onPlayerDeath(client.player, deathMsg);
            }
        }
    }
}
