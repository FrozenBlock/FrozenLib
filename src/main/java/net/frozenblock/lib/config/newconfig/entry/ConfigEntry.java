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
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;

public abstract class ConfigEntry<T> {
	private final Identifier id;
	private final T defaultValue;
	private final boolean syncable;
	private final boolean isModifiable;
	private final ConfigEntryValueNameProvider<T> valueNameProvider;
	private final boolean showTooltip;

	public ConfigEntry(Identifier id, T defaultValue, boolean syncable, boolean isModifiable, ConfigEntryValueNameProvider valueNameProvider, boolean showTooltip) {
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
