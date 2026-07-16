package com.example.addon;

import com.example.addon.modules.NoClip;
import com.example.addon.modules.OreESP;
import com.example.addon.modules.PlayerESP;
import com.example.addon.modules.SpawnerFinder;
import meteordevelopment.meteorclient.addons.MeteorAddon;
import meteordevelopment.meteorclient.systems.modules.Category;
import meteordevelopment.meteorclient.systems.modules.Modules;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AddonTemplate extends MeteorAddon {
    // Logger, use this instead of System.out.println()
    public static final Logger LOG = LogManager.getLogger("SpawnerUtils");

    // Custom category shown in the module list / search
    public static final Category CATEGORY = new Category("SpawnerUtils");

    @Override
    public void onInitialize() {
        LOG.info("Initializing SpawnerUtils");

        // Register modules here
        Modules.get().add(new SpawnerFinder());
        Modules.get().add(new NoClip());
        Modules.get().add(new OreESP());
        Modules.get().add(new PlayerESP());
    }

    @Override
    public void onRegisterCategories() {
        Modules.registerCategory(CATEGORY);
    }

    @Override
    public String getPackage() {
        return "com.example.addon";
    }
}
