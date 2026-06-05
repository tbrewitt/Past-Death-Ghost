package com.pastdeathghost.mixin.client;

import com.pastdeathghost.client.entity.GhostPlayerEntity;
import com.pastdeathghost.client.render.GhostRenderCommandQueue;
import com.pastdeathghost.client.render.GhostRenderState;
import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.client.render.state.CameraRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityRenderer.class)
public abstract class EntityRendererMixin<T extends Entity, S extends EntityRenderState> {
    private static final int GHOST_LABEL_ALPHA = 0x26;

    @Inject(method = "updateRenderState(Lnet/minecraft/entity/Entity;Lnet/minecraft/client/render/entity/state/EntityRenderState;F)V", at = @At("TAIL"))
    private void past_death_ghost$updateGhostRenderState(T entity, S state, float tickDelta, CallbackInfo ci) {
        if (entity instanceof GhostPlayerEntity) {
            state.displayName = entity.getCustomName();

            state.invisible = true;
            if (state instanceof LivingEntityRenderState livingState) {
                livingState.invisibleToPlayer = false;
            }
            if (state instanceof GhostRenderState ghostState) {
                ghostState.past_death_ghost$setGhost(true);
            }
        }
    }

    @Inject(method = "shouldRender", at = @At("HEAD"), cancellable = true)
    private void past_death_ghost$preventCulling(T entity, Frustum frustum, double x, double y, double z, CallbackInfoReturnable<Boolean> cir) {
        if (entity instanceof GhostPlayerEntity) {
            cir.setReturnValue(true);
        }
    }

    @Inject(method = "renderLabelIfPresent", at = @At("HEAD"), cancellable = true)
    private void past_death_ghost$renderTranslucentGhostLabel(S state, MatrixStack matrices, OrderedRenderCommandQueue queue, CameraRenderState cameraRenderState, CallbackInfo ci) {
        if (state instanceof GhostRenderState ghostState && ghostState.past_death_ghost$isGhost() && state.displayName != null) {
            new GhostRenderCommandQueue(queue, GHOST_LABEL_ALPHA)
                    .submitLabel(matrices, state.nameLabelPos, 0, state.displayName, !state.sneaking, state.light, state.squaredDistanceToCamera, cameraRenderState);
            ci.cancel();
        }
    }
}
