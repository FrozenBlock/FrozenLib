/*
 * Copyright (C) 2026 FrozenBlock
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

package net.frozenblock.lib.config.v2.entry.getter;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.frozenblock.lib.config.v2.entry.ConfigEntry;
import net.frozenblock.lib.config.v2.registry.ConfigV2Registry;
import net.frozenblock.lib.config.v2.registry.ID;

public class ConfigEntryGetter<T> {
	public static final Codec<ConfigEntryGetter<?>> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		ID.CODEC.fieldOf("entry").forGetter(getter -> getter.id)
	).apply(instance, ConfigEntryGetter::new));
	final ID id;
	final ConfigEntry<T> entry;

	public ConfigEntryGetter(ID id) {
		this.id = id;
		this.entry = (ConfigEntry<T>) ConfigV2Registry.getEntry(id);
	}

	public ConfigEntryGetter(ConfigEntry<T> entry) {
		this.id = entry.id();
		this.entry = entry;
	}

	public ID id() {
		return this.id;
	}

	public Class<T> type() {
		return entry.entryClass();
	}

	public T get() {
		return this.entry.get();
	}
}
