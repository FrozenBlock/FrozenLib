/*
 * Copyright (C) 2025-2026 FrozenBlock
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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import net.frozenblock.lib.config.newconfig.config.ConfigData;
import net.frozenblock.lib.config.newconfig.entry.property.EntryProperties;
import net.frozenblock.lib.config.newconfig.entry.property.VisibilityPredicate;
import net.frozenblock.lib.config.newconfig.modification.ConfigEntryModification;
import net.frozenblock.lib.config.newconfig.modification.EntryValueHolder;
import net.frozenblock.lib.config.newconfig.registry.ConfigV2Registry;
import net.frozenblock.lib.config.newconfig.registry.ID;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;

public class ConfigEntry<T> {
	private final ConfigData<?> configData;
	private final ID id;
	private final EntryType<T> type;
	private final T defaultValue;
	private final EntryProperties properties;

	private final List<ConfigEntryModification<T>> modifications = new ArrayList<>();

	private T value;
	private Optional<T> modifiedValue = Optional.empty();
	private Optional<T> syncedValue = Optional.empty();
	private boolean dirty;
	private boolean hasCheckedLoad;

	public ConfigEntry(ConfigData<?> data, String id, EntryType<T> type, T defaultValue, EntryProperties properties) {
		this.configData = data;
		this.id = data.id().withSuffix("/" + id);
		this.type = type;
		this.defaultValue = defaultValue;
		this.value = defaultValue;
		this.properties = properties;
		ConfigV2Registry.register(this, this.id);
	}

	public ConfigEntry(ConfigData<?> data, String id, EntryType<T> type, T defaultValue, boolean syncable, boolean modifiable) {
		this(data, id, type, defaultValue, EntryProperties.of(syncable, modifiable));
	}

	public T get() {
		return this.syncedValue.orElse(this.modifiedValue.orElseGet(this::getActual));
	}

	public T getActual() {
		this.ensureIsLoaded();
		return this.value;
	}

	public T getWithSync() {
		this.ensureIsLoaded();
		return this.syncedValue.orElse(this.value);
	}

	public boolean isSyncable() {
		return this.properties.isSyncable();
	}

	public boolean isSynced() {
		return this.syncedValue.isPresent();
	}

	public void setValue(T value) {
		this.setValue(value, true);
	}

	public void setValue(T value, boolean markDirty) {
		this.ensureIsLoaded();
		this.value = value;
		if (markDirty) this.markDirty();
	}

	public void ensureIsLoaded() {
		if (this.hasCheckedLoad) return;
		this.hasCheckedLoad = true;
		this.configData.loadEntry(this, true);
	}

	public void setSyncedValue(T value) {
		if (!this.properties.isSyncable()) return;
		this.syncedValue = Optional.of(value);
	}

	public void removeSync() {
		this.syncedValue = Optional.empty();
	}

	public void modify(Consumer<EntryValueHolder<T>> modification) {
		this.modifications.add(new ConfigEntryModification<>(modification));
		this.invalidateModifications();
	}

	public void invalidateModifications() {
		this.modifiedValue = Optional.ofNullable(ConfigEntryModification.modifyEntry(this, this.getActual()));
	}

	public List<ConfigEntryModification<T>> modifications() {
		return this.modifications;
	}

	public boolean isUnsaved() {
		return this.dirty;
	}

	public boolean isSaved() {
		return !this.dirty;
	}

	public void markDirty() {
		this.dirty = true;
	}

	public void unmarkDirty() {
		this.dirty = false;
	}

	public Class<T> entryClass() {
		return (Class<T>) this.defaultValue.getClass();
	}

	protected Component displayName() {
		return Component.translatable("option." + this.id.namespace() + "." + this.id.path().replace("/", "."));
	}

	protected Component tooltip() {
		return Component.translatable("tooltip." + this.id.namespace() + "." + this.id.path().replace("/", "."));
	}

	public boolean hasComment() {
		return this.properties.hasComment();
	}

	public ConfigData<?> configData() {
		return this.configData;
	}

	public ID id() {
		return this.id;
	}

	public T defaultValue() {
		return this.defaultValue;
	}

	public Optional<String> comment() {
		return this.properties.getComment();
	}

	public Codec<T> codec() {
		return this.type.getCodec();
	}

	public StreamCodec<? extends ByteBuf, T> streamCodec() {
		return this.type.getStreamCodec();
	}

	public static class Builder<T> {
		final ConfigData<?> data;
		String id = null;
		EntryType<T> type = null;
		T defaultValue = null;
		EntryProperties.Builder properties = EntryProperties.builder();

		public Builder(ConfigData<?> data) {
			this.data = data;
		}

		public Builder<T> id(String id) {
			this.id = id;
			return this;
		}

		public Builder<T> type(EntryType<T> type) {
			this.type = type;
			return this;
		}

		public Builder<T> defaultValue(T defaultValue) {
			this.defaultValue = defaultValue;
			return this;
		}

		public Builder<T> properties(EntryProperties.Builder properties) {
			this.properties = properties;
			return this;
		}

		public Builder<T> syncable(boolean syncable) {
			if (this.properties == null) {
				this.properties = EntryProperties.builder();
			}
			this.properties.syncable(syncable);
			return this;
		}

		public Builder<T> modifiable(boolean modifiable) {
			if (this.properties == null) {
				this.properties = EntryProperties.builder();
			}
			this.properties.modifiable(modifiable);
			return this;
		}

		public Builder<T> comment(String comment) {
			if (this.properties == null) {
				this.properties = EntryProperties.builder();
			}
			this.properties.comment(comment);
			return this;
		}

		public Builder<T> visibilityPredicate(VisibilityPredicate predicate) {
			if (this.properties == null) {
				this.properties = EntryProperties.builder();
			}
			this.properties.visibilityPredicate(predicate);
			return this;
		}

		public ConfigEntry<T> build() {
			if (this.id == null) throw new IllegalStateException("Entry ID cannot be null!");
			if (this.type == null) throw new IllegalStateException("Entry type cannot be null!");
			if (this.defaultValue == null) throw new IllegalStateException("Entry Default value cannot be null!");
			if (this.properties == null) throw new IllegalStateException("Entry Properties cannot be null!");

			return new ConfigEntry<>(this.data, this.id, this.type, this.defaultValue, this.properties.build());
		}
	}
}
