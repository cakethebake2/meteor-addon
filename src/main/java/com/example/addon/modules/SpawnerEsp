package com.example.addon.modules;

import com.example.addon.AddonTemplate;
import meteordevelopment.meteorclient.events.render.Render3DEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.renderer.ShapeMode;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.systems.waypoints.Waypoint;
import meteordevelopment.meteorclient.systems.waypoints.Waypoints;
import meteordevelopment.meteorclient.utils.player.ChatUtils;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.block.entity.MobSpawnerBlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.chunk.WorldChunk;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Scans loaded chunks for mob spawner block entities and renders an ESP
 * box / tracer to each one, even through walls - useful for locating
 * spawners buried in deepslate/caves without digging around blind.
 */
public class SpawnerFinder extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();
    private final SettingGroup sgRender = settings.createGroup("Render");

    // ---- General ----

    private final Setting<Integer> chunkRange = sgGeneral.add(new IntSetting.Builder()
        .name("chunk-range")
        .description("How many chunks around you to scan, in each direction.")
        .defaultValue(6)
        .min(1)
        .sliderMax(16)
        .build()
    );

    private final Setting<Boolean> notify = sgGeneral.add(new BoolSetting.Builder()
        .name("notify-in-chat")
        .description("Sends a chat message the first time a new spawner is found.")
        .defaultValue(true)
        .build()
    );

    private final Setting<Boolean> showMobType = sgGeneral.add(new BoolSetting.Builder()
        .name("show-mob-type")
        .description("Reads which mob the spawner will spawn and shows it in chat/waypoint name.")
        .defaultValue(true)
        .build()
    );

    private final Setting<Boolean> waypoint = sgGeneral.add(new BoolSetting.Builder()
        .name("create-waypoint")
        .description("Automatically adds a Meteor waypoint for each newly found spawner.")
        .defaultValue(false)
        .build()
    );

    // ---- Render ----

    private final Setting<ShapeMode> shapeMode = sgRender.add(new EnumSetting.Builder<ShapeMode>()
        .name("shape-mode")
        .description("How the ESP box is rendered.")
        .defaultValue(ShapeMode.Both)
        .build()
    );

    private final Setting<Boolean> tracers = sgRender.add(new BoolSetting.Builder()
        .name("tracers")
        .description("Renders a line from your crosshair to each spawner.")
        .defaultValue(true)
        .build()
    );

    private final Setting<SettingColor> sideColor = sgRender.add(new ColorSetting.Builder()
        .name("side-color")
        .description("Fill color of the ESP box.")
        .defaultValue(new SettingColor(255, 40, 40, 40))
        .build()
    );

    private final Setting<SettingColor> lineColor = sgRender.add(new ColorSetting.Builder()
        .name("line-color")
        .description("Outline/tracer color.")
        .defaultValue(new SettingColor(255, 40, 40, 255))
        .build()
    );

    // Positions we've already found, so we only notify/waypoint once per spawner
    private final Set<BlockPos> found = new HashSet<>();
    // Positions found this run, refreshed on a timer so removed spawners drop off
    private final Set<BlockPos> active = new HashSet<>();
    // Cached "mob type" label per spawner, so we don't recompute it every render frame
    private final Map<BlockPos, String> mobTypes = new HashMap<>();
    private int tickTimer;

    public SpawnerFinder() {
        super(AddonTemplate.CATEGORY, "spawner-finder", "Highlights nearby mob spawners through terrain.");
    }

    @Override
    public void onActivate() {
        found.clear();
        active.clear();
        mobTypes.clear();
        tickTimer = 0;
    }

    @EventHandler
    private void onTick(TickEvent.Post event) {
        // Rescan every 20 ticks (~1s) instead of every tick, this is cheap but no need to hammer it
        if (tickTimer-- > 0) return;
        tickTimer = 20;

        active.clear();

        int cx = mc.player.getChunkPos().x;
        int cz = mc.player.getChunkPos().z;

        for (int x = -chunkRange.get(); x <= chunkRange.get(); x++) {
            for (int z = -chunkRange.get(); z <= chunkRange.get(); z++) {
                ChunkPos pos = new ChunkPos(cx + x, cz + z);
                if (!mc.world.getChunkManager().isChunkLoaded(pos.x, pos.z)) continue;

                WorldChunk chunk = mc.world.getChunk(pos.x, pos.z);

                chunk.getBlockEntities().forEach((blockPos, blockEntity) -> {
                    if (!(blockEntity instanceof MobSpawnerBlockEntity spawner)) return;

                    BlockPos immutable = blockPos.toImmutable();
                    active.add(immutable);

                    // getRenderedEntity() is the same entity the client shows spinning
                    // inside the spawner cage - reading its type tells us what it'll spawn
                    String mobType = "Unknown";
                    if (showMobType.get()) {
                        Entity rendered = spawner.getLogic().getRenderedEntity(mc.world);
                        if (rendered != null) mobType = rendered.getType().getName().getString();
                    }

                    boolean isNew = found.add(immutable);
                    if (isNew) mobTypes.put(immutable, mobType);

                    if (isNew && notify.get()) {
                        ChatUtils.info("SpawnerFinder", "Found (highlight)%s(default) spawner at (highlight)%s, %s, %s",
                            mobType, immutable.getX(), immutable.getY(), immutable.getZ());
                    }

                    if (isNew && waypoint.get()) {
                        Waypoints.get().add(new Waypoint.Builder()
                            .name("Spawner (" + mobType + ")")
                            .pos(immutable)
                            .build()
                        );
                    }
                });
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
                    (float) (mc.player.getX()), (float) (mc.player.getY() + mc.player.getStandingEyeHeight()), (float) (mc.player.getZ()),
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
