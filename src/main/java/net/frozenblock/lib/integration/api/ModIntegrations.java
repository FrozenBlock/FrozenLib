package net.frozenblock.lib.integration.api;

import net.frozenblock.lib.integration.impl.ModIntegrationSupplier;
import net.frozenblock.lib.registry.api.FrozenRegistry;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;

import java.util.List;
import java.util.function.Supplier;

public final class ModIntegrations {

    private ModIntegrations() {
        throw new UnsupportedOperationException("ModIntegrations contains only static declarations.");
    }

    /**
     * Registers a mod integration class
     *
     * @param integration   The mod integration class to register
     * @param srcModID      The id of the mod registering the mod integration
	 * @param modID      The id of the mod being integrated
     * @return
     */
    public static ModIntegrationSupplier register(Supplier<ModIntegration> integration, String srcModID, String modID) {
        return Registry.register(FrozenRegistry.MOD_INTEGRATION, new ResourceLocation(srcModID, modID), new ModIntegrationSupplier(integration, modID));
    }

    public static List<ModIntegrationSupplier> getIntegrationSuppliers() {
        return FrozenRegistry.MOD_INTEGRATION.stream().toList();
    }

    /**
     * Initialize all mod integrations.
     */
    public static void initialize() {
        for (var integration : FrozenRegistry.MOD_INTEGRATION) {
            integration.getIntegration().init();
        }
    }

}
