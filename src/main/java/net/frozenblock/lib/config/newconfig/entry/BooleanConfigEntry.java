package net.frozenblock.lib.config.newconfig.entry;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.frozenblock.lib.config.newconfig.tooltip.ConfigEntryValueNameProvider;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;

public class BooleanConfigEntry extends ConfigEntry<Boolean> {

	public BooleanConfigEntry(Identifier id, boolean defaultValue, boolean syncable, boolean isModifiable, ConfigEntryValueNameProvider valueNameProvider, boolean showTooltip) {
		super(id, defaultValue, syncable, isModifiable, valueNameProvider, showTooltip);
	}

	/**
	 * Creates a boolean entry with the default settings: syncable, modifiable, value names provided by {@link ConfigEntryValueNameProvider#BOOL}, and shows tooltip.
	 * @param id The {@link Identifier} to register the entry under.
	 * @param defaultValue The default value of the entry.
	 * @return A new boolean entry.
	 */
	public static BooleanConfigEntry createSimple(Identifier id, boolean defaultValue) {
		return new BooleanConfigEntry(id, defaultValue, true, true, ConfigEntryValueNameProvider.BOOL, true);
	}

	@Override
	public Codec<Boolean> getCodec() {
		return Codec.BOOL;
	}

	@Override
	public StreamCodec<ByteBuf, Boolean> getStreamCodec() {
		return ByteBufCodecs.BOOL;
	}
}
