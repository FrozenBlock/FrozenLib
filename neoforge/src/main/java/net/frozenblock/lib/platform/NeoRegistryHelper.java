package net.frozenblock.lib.platform;

import net.frozenblock.lib.platform.service.RegistryHelper;
import net.minecraft.core.Registry;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.registries.DeferredRegister;

public class NeoRegistryHelper implements RegistryHelper {
	@Override
	public <T> T register(Registry<T> registry, Identifier id, T value) {
		return DeferredRegister.create(registry, id.getNamespace()).register(id.getPath(), () -> value).get();
	}
}
