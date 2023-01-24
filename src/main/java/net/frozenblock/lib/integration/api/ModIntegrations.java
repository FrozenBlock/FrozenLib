package net.frozenblock.lib.integration.api;

import net.frozenblock.lib.registry.api.FrozenRegistry;
import net.minecraft.core.Registry;

import java.util.List;

public final class ModIntegrations {

    private ModIntegrations() {
        throw new UnsupportedOperationException("ModIntegrations contains only static declarations.");
    }

    /**
     * Registers a mod integration class
     *
     * @param integration   The mod integration class to register
     * @param srcModID      The id of the mod registering the mod integration
     * @param targetModID   The mod id to add integration for
     * @return
     */
    public static ModIntegration register(ModIntegration integration, String srcModID, String targetModID) {
        return Registry.register(FrozenRegistry.MOD_INTEGRATION, srcModID + "/" + targetModID, integration);
    }

    public static List<ModIntegration> getIntegrations() {
        return FrozenRegistry.MOD_INTEGRATION.stream().toList();
    }

    /**
     * Initialize all mod integrations.
     */
    public static void initialize() {
        for (var integration : FrozenRegistry.MOD_INTEGRATION) {
            if (integration.modLoaded()) {
                integration.init();
            }
        }
    }
}
