package net.frozenblock.lib.config.api;

import net.frozenblock.lib.config.api.client.FrozenConfig;

@FunctionalInterface
public interface ConfigBuilder<T> {
	FrozenConfig.Builder build(T defaultInstance, T config, FrozenConfig.Builder builder);
}
