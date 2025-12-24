package net.frozenblock.lib.config.newconfig.button;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.frozenblock.lib.config.newconfig.AbstractConfigEntry;

@Environment(EnvType.CLIENT)
public abstract class AbstractConfigEntryButton<T> {
	private final AbstractConfigEntry<T> entry;

	public AbstractConfigEntryButton(AbstractConfigEntry<T> entry) {
		this.entry = entry;
	}

}
