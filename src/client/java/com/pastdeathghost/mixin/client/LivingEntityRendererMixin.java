package com.pastdeathghost.mixin.client;

import com.pastdeathghost.client.render.GhostRenderState;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntityRenderer.class)
public abstract class LivingEntityRendererMixin<T extends LivingEntity, S extends LivingEntityRenderState> {

    /**
     * When the entity is flagged as a ghost (state.invisible=true, invisibleToPlayer=false),
     * vanilla already passes translucent=true into getRenderLayer.
     * We override here to swap the default player texture for our ghost.png,
     * keeping the entityTranslucent layer so alpha blending works.
     */
    @Inject(method = "getRenderLayer", at = @At("RETURN"), cancellable = true)
    private void past_death_ghost$useGhostTexture(
            S state, boolean showBody, boolean translucent, boolean showOutline,
            CallbackInfoReturnable<RenderLayer> cir) {
        if (state instanceof GhostRenderState ghostState && ghostState.past_death_ghost$isGhost()) {
            Identifier texture = Identifier.of("past_death_ghost", "textures/entity/ghost.png");
            cir.setReturnValue(RenderLayers.entityTranslucent(texture));
        }
    }
}
