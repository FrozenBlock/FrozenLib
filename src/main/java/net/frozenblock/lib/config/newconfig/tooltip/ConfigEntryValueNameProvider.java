package net.frozenblock.lib.config.newconfig.tooltip;

import net.minecraft.network.chat.Component;

public abstract class ConfigEntryValueNameProvider<T> {

	public abstract Component getValueName(T value);

}
