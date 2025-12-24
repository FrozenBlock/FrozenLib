package net.frozenblock.lib.config.newconfig.test;

import net.frozenblock.lib.FrozenLibConstants;
import net.frozenblock.lib.config.newconfig.entry.ConfigEntry;
import net.frozenblock.lib.config.newconfig.entry.BooleanConfigEntry;
import net.frozenblock.lib.registry.FrozenLibRegistries;
import net.minecraft.core.Registry;

public class TestConfig {
	public static final BooleanConfigEntry TEST = BooleanConfigEntry.createSimple(FrozenLibConstants.id("test"), true);

	public static ConfigEntry<?> register(ConfigEntry<?> entry) {
		return Registry.register(FrozenLibRegistries.CONFIG_ENTRY, entry.getId(), entry);
	}
}
