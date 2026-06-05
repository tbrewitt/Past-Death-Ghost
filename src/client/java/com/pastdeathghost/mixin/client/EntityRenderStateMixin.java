package com.pastdeathghost.mixin.client;

import com.pastdeathghost.client.render.GhostRenderState;
import net.minecraft.client.render.entity.state.EntityRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(EntityRenderState.class)
public class EntityRenderStateMixin implements GhostRenderState {

    @Unique
    private boolean past_death_ghost$isGhost = false;

    @Override
    public boolean past_death_ghost$isGhost() {
        return this.past_death_ghost$isGhost;
    }

    @Override
    public void past_death_ghost$setGhost(boolean isGhost) {
        this.past_death_ghost$isGhost = isGhost;
    }
}
