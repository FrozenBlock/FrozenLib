package net.frozenblock.lib.config.newconfig;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.frozenblock.lib.config.newconfig.tooltip.ConfigEntryValueNameProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;

public abstract class AbstractConfigEntry<T> {
	private final Identifier id;
	private final T defaultValue;
	private final boolean syncable;
	private final boolean isModifiable;
	private final ConfigEntryValueNameProvider<T> valueNameProvider;
	private final boolean showTooltip;

	public AbstractConfigEntry(Identifier id, T defaultValue, boolean syncable, boolean isModifiable, ConfigEntryValueNameProvider valueNameProvider, boolean showTooltip) {
		this.id = id;
		this.defaultValue = defaultValue;
		this.syncable = syncable;
		this.isModifiable = isModifiable;
		this.valueNameProvider = valueNameProvider;
		this.showTooltip = showTooltip;
	}

	protected Component getDisplayName() {
		return Component.translatable("option." + this.id.getNamespace() + "." + this.id.getPath().replace("/", "."));
	}

	protected Component getTooltip() {
		return Component.translatable("tooltip." + this.id.getNamespace() + "." + this.id.getPath().replace("/", "."));
	}

	public Identifier getId() {
		return this.id;
	}

	public T getDefaultValue() {
		return this.defaultValue;
	}

	public boolean isSyncable() {
		return this.syncable;
	}

	public boolean isModifiable() {
		return this.isModifiable;
	}

	public boolean canShowTooltip() {
		return this.showTooltip;
	}

	public ConfigEntryValueNameProvider<T> getValueNameProvider() {
		return this.valueNameProvider;
	}

	public abstract Codec<T> getCodec();

	public abstract StreamCodec<ByteBuf, T> getStreamCodec();

}
