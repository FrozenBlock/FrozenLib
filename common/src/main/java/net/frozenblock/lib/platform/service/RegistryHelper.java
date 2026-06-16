package net.frozenblock.lib.platform.service;

import net.minecraft.core.Registry;
import net.minecraft.resources.Identifier;

public interface RegistryHelper {

	<T> T register(Registry<T> registry, Identifier id, T value);
}
