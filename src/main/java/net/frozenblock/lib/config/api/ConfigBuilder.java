package net.frozenblock.lib.config.api;

import net.frozenblock.lib.config.api.client.ClientConfig;

@FunctionalInterface
public interface ConfigBuilder<T> {
	ClientConfig.Builder build(T defaultInstance, T config, ClientConfig.Builder builder);
}
