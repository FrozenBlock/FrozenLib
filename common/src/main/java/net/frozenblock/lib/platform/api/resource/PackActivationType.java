package net.frozenblock.lib.platform.api.resource;

public enum PackActivationType {
	NORMAL,
	DEFAULT_ENABLED,
	ALWAYS_ENABLED;

	public boolean isEnabledByDefault() {
		return this == DEFAULT_ENABLED || this == ALWAYS_ENABLED;
	}
}
