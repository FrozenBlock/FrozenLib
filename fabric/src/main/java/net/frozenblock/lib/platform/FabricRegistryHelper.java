package net.frozenblock.lib.platform;

import net.frozenblock.lib.platform.service.RegistryHelper;
import net.minecraft.core.Registry;
import net.minecraft.resources.Identifier;

public class FabricRegistryHelper implements RegistryHelper {
	@Override
	public <T> T register(Registry<T> registry, Identifier id, T value) {
		return Registry.register(registry, id, value);
	}
}
