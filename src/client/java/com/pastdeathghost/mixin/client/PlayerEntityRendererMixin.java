package com.pastdeathghost.mixin.client;

import com.pastdeathghost.client.render.GhostRenderCommandQueue;
import com.pastdeathghost.client.render.GhostRenderState;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import net.minecraft.client.render.state.CameraRenderState;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntityRenderer.class)
public abstract class PlayerEntityRendererMixin {
    private static final int GHOST_LABEL_ALPHA = 0x26;

    @Inject(method = "renderLabelIfPresent", at = @At("HEAD"), cancellable = true)
    private void past_death_ghost$renderTranslucentGhostLabel(
            PlayerEntityRenderState state,
            MatrixStack matrices,
            OrderedRenderCommandQueue queue,
            CameraRenderState cameraRenderState,
            CallbackInfo ci) {
        if (!(state instanceof GhostRenderState ghostState) || !ghostState.past_death_ghost$isGhost()) {
            return;
        }

        GhostRenderCommandQueue ghostQueue = new GhostRenderCommandQueue(queue, GHOST_LABEL_ALPHA);
        matrices.push();
        int y = state.extraEars ? -10 : 0;
        if (state.playerName != null) {
            ghostQueue.submitLabel(matrices, state.nameLabelPos, y, state.playerName, !state.sneaking, state.light, state.squaredDistanceToCamera, cameraRenderState);
            matrices.translate(0.0F, 9.0F * 1.15F * 0.025F, 0.0F);
        }

        if (state.displayName != null) {
            ghostQueue.submitLabel(matrices, state.nameLabelPos, y, state.displayName, !state.sneaking, state.light, state.squaredDistanceToCamera, cameraRenderState);
        }

        matrices.pop();
        ci.cancel();
    }
}
