package com.example.addon.mixin;

import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/**
 * Entity#noClip is protected and normally only true for entities in
 * spectator mode. This accessor lets modules flip it on the player entity
 * directly, which is how NoClip/"Debug" style movement modules work.
 */
@Mixin(Entity.class)
public interface EntityAccessor {
    @Accessor("noClip")
    void meteor$setNoClip(boolean noClip);

    @Accessor("noClip")
    boolean meteor$getNoClip();
}
