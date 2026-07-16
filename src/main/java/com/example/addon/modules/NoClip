package com.example.addon.modules;

import com.example.addon.AddonTemplate;
import com.example.addon.mixin.EntityAccessor;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.DoubleSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Module;
import net.minecraft.entity.player.PlayerAbilities;

/**
 * Disables collision on the player so you can fly straight through terrain -
 * deepslate, stone, bedrock, whatever - to scout what's around without
 * digging. Restores your original flight/collision state on deactivate.
 */
public class NoClip extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<Double> flySpeed = sgGeneral.add(new DoubleSetting.Builder()
        .name("fly-speed")
        .description("Flight speed while NoClip is active.")
        .defaultValue(0.5)
        .min(0.05)
        .sliderMax(3)
        .build()
    );

    private final Setting<Boolean> restoreOnDeactivate = sgGeneral.add(new BoolSetting.Builder()
        .name("restore-on-toggle-off")
        .description("Teleports/settles you safely and restores normal collision when you turn this off.")
        .defaultValue(true)
        .build()
    );

    // Saved state so we can restore it on deactivate
    private boolean prevFlying;
    private boolean prevAllowFlying;
    private float prevFlySpeed;

    public NoClip() {
        super(AddonTemplate.CATEGORY, "no-clip", "Fly through blocks freely, useful for scouting underground.");
    }

    @Override
    public void onActivate() {
        if (mc.player == null) return;

        PlayerAbilities abilities = mc.player.getAbilities();

        prevFlying = abilities.flying;
        prevAllowFlying = abilities.allowFlying;
        prevFlySpeed = abilities.getFlySpeed();

        abilities.allowFlying = true;
        abilities.flying = true;
        abilities.setFlySpeed(flySpeed.get().floatValue());
        mc.player.sendAbilitiesUpdate();

        ((EntityAccessor) (Object) mc.player).meteor$setNoClip(true);
    }

    @Override
    public void onDeactivate() {
        if (mc.player == null) return;

        ((EntityAccessor) (Object) mc.player).meteor$setNoClip(false);

        if (restoreOnDeactivate.get()) {
            PlayerAbilities abilities = mc.player.getAbilities();

            // Don't leave the player permanently flying if they weren't
            // before, unless they're actually in creative/spectator
            if (!mc.player.isCreative() && !mc.player.isSpectator()) {
                abilities.flying = prevFlying;
                abilities.allowFlying = prevAllowFlying;
            }

            abilities.setFlySpeed(prevFlySpeed);
            mc.player.sendAbilitiesUpdate();
        }
    }
}
