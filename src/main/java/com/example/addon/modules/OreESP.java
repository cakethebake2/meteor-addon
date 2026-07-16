package com.example.addon.modules;

import com.example.addon.AddonTemplate;
import meteordevelopment.meteorclient.events.render.Render3DEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.renderer.ShapeMode;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Highlights valuable ores through terrain, like ancient debris and
 * diamonds, within a limited cube around the player. Note: on servers
 * with server-side anti-xray, blocks the anti-xray hides are never sent
 * to your client as their real ID in the first place, so this will only
 * ever show ores the server has already decided to reveal to you.
 */
public class OreESP extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();
    private final SettingGroup sgRender = settings.createGroup("Render");

    // ---- General ----

    private final Setting<List<Block>> blocks = sgGeneral.add(new BlockListSetting.Builder()
        .name("blocks")
        .description("Which blocks to highlight.")
        .defaultValue(
            Blocks.ANCIENT_DEBRIS,
            Blocks.DIAMOND_ORE, Blocks.DEEPSLATE_DIAMOND_ORE,
            Blocks.EMERALD_ORE, Blocks.DEEPSLATE_EMERALD_ORE
        )
        .build()
    );

    private final Setting<Integer> range = sgGeneral.add(new IntSetting.Builder()
        .name("range")
        .description("Search radius in blocks around you. Kept small on purpose - full-world scans are expensive.")
        .defaultValue(24)
        .min(4)
        .sliderMax(48)
        .build()
    );

    // ---- Render ----

    private final Setting<ShapeMode> shapeMode = sgRender.add(new EnumSetting.Builder<ShapeMode>()
        .name("shape-mode")
        .defaultValue(ShapeMode.Both)
        .build()
    );

    private final Setting<Boolean> tracers = sgRender.add(new BoolSetting.Builder()
        .name("tracers")
        .defaultValue(false)
        .build()
    );

    private final Setting<SettingColor> sideColor = sgRender.add(new ColorSetting.Builder()
        .name("side-color")
        .defaultValue(new SettingColor(80, 200, 255, 40))
        .build()
    );

    private final Setting<SettingColor> lineColor = sgRender.add(new ColorSetting.Builder()
        .name("line-color")
        .defaultValue(new SettingColor(80, 200, 255, 255))
        .build()
    );

    private final Set<BlockPos> active = new HashSet<>();
    private int tickTimer;

    public OreESP() {
        super(AddonTemplate.CATEGORY, "ore-esp", "Highlights valuable ores near you through terrain.");
    }

    @Override
    public void onActivate() {
        active.clear();
        tickTimer = 0;
    }

    @EventHandler
    private void onTick(TickEvent.Post event) {
        if (tickTimer-- > 0) return;
        tickTimer = 10; // rescan twice a second, small radius so this is cheap

        active.clear();

        Set<Block> targets = new HashSet<>(blocks.get());
        if (targets.isEmpty()) return;

        BlockPos center = mc.player.getBlockPos();
        int r = range.get();

        for (BlockPos pos : BlockPos.iterate(
            center.getX() - r, center.getY() - r, center.getZ() - r,
            center.getX() + r, center.getY() + r, center.getZ() + r
        )) {
            if (targets.contains(mc.world.getBlockState(pos).getBlock())) {
                active.add(pos.toImmutable());
            }
        }
    }

    @EventHandler
    private void onRender(Render3DEvent event) {
        for (BlockPos pos : active) {
            event.renderer.box(
                pos.getX(), pos.getY(), pos.getZ(),
                pos.getX() + 1, pos.getY() + 1, pos.getZ() + 1,
                sideColor.get(), lineColor.get(), shapeMode.get(), 0
            );

            if (tracers.get()) {
                event.renderer.line(
                    (float) mc.player.getX(), (float) (mc.player.getY() + mc.player.getStandingEyeHeight()), (float) mc.player.getZ(),
                    pos.getX() + 0.5f, pos.getY() + 0.5f, pos.getZ() + 0.5f,
                    lineColor.get()
                );
            }
        }
    }

    @Override
    public String getInfoString() {
        return String.valueOf(active.size());
    }
}
