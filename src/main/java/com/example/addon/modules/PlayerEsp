package com.example.addon.modules;

import com.example.addon.AddonTemplate;
import meteordevelopment.meteorclient.events.render.Render3DEvent;
import meteordevelopment.meteorclient.renderer.ShapeMode;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.client.network.AbstractClientPlayerEntity;

/**
 * Shows the box/tracer positions of other nearby players through walls -
 * plain situational awareness (who's around me right now), not a way to
 * locate anyone's base or belongings.
 */
public class PlayerESP extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<Double> range = sgGeneral.add(new DoubleSetting.Builder()
        .name("range")
        .description("Only render players within this distance.")
        .defaultValue(64)
        .min(8)
        .sliderMax(256)
        .build()
    );

    private final Setting<ShapeMode> shapeMode = sgGeneral.add(new EnumSetting.Builder<ShapeMode>()
        .name("shape-mode")
        .defaultValue(ShapeMode.Both)
        .build()
    );

    private final Setting<Boolean> tracers = sgGeneral.add(new BoolSetting.Builder()
        .name("tracers")
        .defaultValue(true)
        .build()
    );

    private final Setting<SettingColor> sideColor = sgGeneral.add(new ColorSetting.Builder()
        .name("side-color")
        .defaultValue(new SettingColor(255, 220, 40, 40))
        .build()
    );

    private final Setting<SettingColor> lineColor = sgGeneral.add(new ColorSetting.Builder()
        .name("line-color")
        .defaultValue(new SettingColor(255, 220, 40, 255))
        .build()
    );

    public PlayerESP() {
        super(AddonTemplate.CATEGORY, "player-esp", "Shows nearby players through walls.");
    }

    @EventHandler
    private void onRender(Render3DEvent event) {
        double rangeSq = range.get() * range.get();

        for (AbstractClientPlayerEntity player : mc.world.getPlayers()) {
            if (player == mc.player) continue;
            if (player.squaredDistanceTo(mc.player) > rangeSq) continue;

            double x = player.prevX + (player.getX() - player.prevX) * event.tickDelta;
            double y = player.prevY + (player.getY() - player.prevY) * event.tickDelta;
            double z = player.prevZ + (player.getZ() - player.prevZ) * event.tickDelta;

            double w = player.getWidth() / 2.0;
            double h = player.getHeight();

            event.renderer.box(
                x - w, y, z - w,
                x + w, y + h, z + w,
                sideColor.get(), lineColor.get(), shapeMode.get(), 0
            );

            if (tracers.get()) {
                event.renderer.line(
                    (float) mc.player.getX(), (float) (mc.player.getY() + mc.player.getStandingEyeHeight()), (float) mc.player.getZ(),
                    (float) x, (float) (y + h / 2), (float) z,
                    lineColor.get()
                );
            }
        }
    }
}
