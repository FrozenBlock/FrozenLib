package net.frozenblock.lib;

import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.frozenblock.lib.integration.api.ModIntegrations;

@Environment(EnvType.SERVER)
public class FrozenServer implements DedicatedServerModInitializer {

    @Override
    public void onInitializeServer() {
        ModIntegrations.initialize(); // Mod integrations must run after normal mod initialization
    }
}
