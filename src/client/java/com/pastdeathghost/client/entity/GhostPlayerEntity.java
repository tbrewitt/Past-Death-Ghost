package com.pastdeathghost.client.entity;

import net.minecraft.client.network.OtherClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import com.mojang.authlib.GameProfile;
import net.minecraft.entity.Entity;

/**
 * A custom client-only player entity subclass.
 * Used to identify ghost player models and apply custom rendering (translucency).
 */
public class GhostPlayerEntity extends OtherClientPlayerEntity {
    public GhostPlayerEntity(ClientWorld world, GameProfile profile) {
        super(world, profile);
        this.noClip = true;
    }

    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    public boolean canBeHitByProjectile() {
        return false;
    }

    @Override
    public boolean isAttackable() {
        return false;
    }

    @Override
    public boolean shouldRender(double distance) {
        return true;
    }

    @Override
    public boolean shouldRender(double cameraX, double cameraY, double cameraZ) {
        return true;
    }
}
