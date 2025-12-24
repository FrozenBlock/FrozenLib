/*
 * Copyright (C) 2025 FrozenBlock
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

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
