package com.pastdeathghost.mixin.client;

import com.pastdeathghost.client.entity.GhostPlayerEntity;
import com.pastdeathghost.client.render.GhostRenderCommandQueue;
import com.pastdeathghost.client.render.GhostRenderState;
import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.command.RenderCommandQueue;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.state.CameraRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityRenderer.class)
public abstract class EntityRendererMixin<T extends Entity, S extends EntityRenderState> {

    @Inject(method = "updateRenderState(Lnet/minecraft/entity/Entity;Lnet/minecraft/client/render/entity/state/EntityRenderState;F)V", at = @At("TAIL"))
    private void past_death_ghost$updateGhostRenderState(T entity, S state, float tickDelta, CallbackInfo ci) {
        if (state instanceof GhostRenderState ghostState) {
            ghostState.past_death_ghost$setGhost(entity instanceof GhostPlayerEntity);
        }
    }

    @Inject(method = "shouldRender", at = @At("HEAD"), cancellable = true)
    private void past_death_ghost$preventCulling(T entity, Frustum frustum, double x, double y, double z, CallbackInfoReturnable<Boolean> cir) {
        if (entity instanceof GhostPlayerEntity) {
            cir.setReturnValue(true);
        }
    }

    @ModifyVariable(
        method = "render(Lnet/minecraft/client/render/entity/state/EntityRenderState;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/command/OrderedRenderCommandQueue;Lnet/minecraft/client/render/state/CameraRenderState;)V",
        at = @At("HEAD"),
        argsOnly = true
    )
    private OrderedRenderCommandQueue past_death_ghost$wrapQueue(OrderedRenderCommandQueue queue, EntityRenderState state) {
        if (state instanceof GhostRenderState ghostState && ghostState.past_death_ghost$isGhost()) {
            return new GhostRenderCommandQueue(queue, 120);
        }
        return queue;
    }
}
